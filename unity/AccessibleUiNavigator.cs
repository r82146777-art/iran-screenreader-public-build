using System;
using System.Collections.Generic;
using System.Reflection;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

/// <summary>
/// Accessibility navigation prototype for Unity uGUI screens.
/// Add one instance to a persistent scene object. This is source code only;
/// it must be integrated into the original Unity project and tested per screen.
/// </summary>
public sealed class AccessibleUiNavigator : MonoBehaviour
{
    [Header("Accessibility")]
    [SerializeField] private bool startEnabled = false;
    [SerializeField] private float swipeThresholdPixels = 70f;
    [SerializeField] private float doubleTapWindow = 0.35f;
    [SerializeField] private float tripleTapWindow = 1.1f;

    private readonly List<Selectable> controls = new List<Selectable>();
    private AndroidJavaObject tts;
    private bool ttsReady;
    private bool enabledMode;
    private int selectedIndex = -1;
    private float lastTapTime = -10f;
    private int rapidTapCount;
    private Vector2 touchStart;
    private bool trackingTouch;

    private void Awake()
    {
        enabledMode = startEnabled;
        DontDestroyOnLoad(gameObject);
        InitTts();
        RefreshControls();
    }

    private void OnEnable()
    {
        RefreshControls();
    }

    private void OnDestroy()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        try { if (tts != null) { tts.Call("stop"); tts.Call("shutdown"); tts.Dispose(); } }
        catch (Exception e) { Debug.LogWarning("TTS shutdown: " + e.Message); }
#endif
    }

    private void Update()
    {
        if (Input.GetKeyDown(KeyCode.LeftArrow) || Input.GetKeyDown(KeyCode.UpArrow))
            Move(-1);
        if (Input.GetKeyDown(KeyCode.RightArrow) || Input.GetKeyDown(KeyCode.DownArrow))
            Move(1);
        if (Input.GetKeyDown(KeyCode.Return) || Input.GetKeyDown(KeyCode.KeypadEnter))
            ActivateCurrent();

        HandleTouchGestures();
    }

    public void SetAccessibilityMode(bool enabled)
    {
        enabledMode = enabled;
        Say(enabledMode ? "حالت دسترس‌پذیری روشن شد" : "حالت دسترس‌پذیری خاموش شد");
        if (enabledMode)
        {
            RefreshControls();
            if (controls.Count > 0) SelectIndex(0);
            else Say("کنترل قابل استفاده‌ای در این صفحه پیدا نشد");
        }
    }

    public void ToggleAccessibilityMode() => SetAccessibilityMode(!enabledMode);

    private void HandleTouchGestures()
    {
        if (Input.touchCount == 3 && Input.GetTouch(0).phase == TouchPhase.Began)
        {
            ToggleAccessibilityMode();
            trackingTouch = false;
            return;
        }
        if (!enabledMode || Input.touchCount != 1) return;

        Touch touch = Input.GetTouch(0);
        if (touch.phase == TouchPhase.Began)
        {
            touchStart = touch.position;
            trackingTouch = true;
        }
        else if (trackingTouch && (touch.phase == TouchPhase.Ended || touch.phase == TouchPhase.Canceled))
        {
            trackingTouch = false;
            Vector2 delta = touch.position - touchStart;
            if (Mathf.Abs(delta.x) >= swipeThresholdPixels && Mathf.Abs(delta.x) > Mathf.Abs(delta.y))
            {
                Move(delta.x < 0 ? 1 : -1);
                return;
            }

            if (delta.magnitude < swipeThresholdPixels)
            {
                float now = Time.unscaledTime;
                if (now - lastTapTime <= doubleTapWindow)
                {
                    rapidTapCount++;
                    if (rapidTapCount >= 2)
                    {
                        rapidTapCount = 0;
                        ActivateCurrent();
                    }
                }
                else rapidTapCount = 1;
                lastTapTime = now;
            }
        }
    }

    private void RefreshControls()
    {
        controls.Clear();
        Selectable[] found = FindObjectsOfType<Selectable>(true);
        foreach (Selectable item in found)
        {
            if (item == null || !item.gameObject.activeInHierarchy || !item.IsInteractable()) continue;
            controls.Add(item);
        }
        controls.Sort((a, b) =>
        {
            int ay = Mathf.RoundToInt(-a.transform.position.y * 100f);
            int by = Mathf.RoundToInt(-b.transform.position.y * 100f);
            if (ay != by) return ay.CompareTo(by);
            return a.transform.position.x.CompareTo(b.transform.position.x);
        });
        if (selectedIndex >= controls.Count) selectedIndex = controls.Count - 1;
    }

    private void Move(int delta)
    {
        if (!enabledMode) return;
        RefreshControls();
        if (controls.Count == 0) { Say("در این صفحه گزینه‌ای پیدا نشد"); return; }
        int next = selectedIndex < 0 ? 0 : (selectedIndex + delta + controls.Count) % controls.Count;
        SelectIndex(next);
    }

    private void SelectIndex(int index)
    {
        if (index < 0 || index >= controls.Count) return;
        selectedIndex = index;
        Selectable item = controls[selectedIndex];
        if (EventSystem.current != null) EventSystem.current.SetSelectedGameObject(item.gameObject);
        item.Select();
        Say(GetAccessibleLabel(item));
    }

    private string GetAccessibleLabel(Selectable item)
    {
        if (item == null) return "گزینه نامشخص";
        // Prefer visible Unity UI text, including TextMeshPro without a hard dependency.
        foreach (Component component in item.GetComponentsInChildren<Component>(true))
        {
            if (component == null) continue;
            Type type = component.GetType();
            if (type.FullName == "TMPro.TMP_Text" || type == typeof(Text))
            {
                PropertyInfo textProp = type.GetProperty("text");
                string value = textProp == null ? null : textProp.GetValue(component, null) as string;
                if (!string.IsNullOrWhiteSpace(value)) return value.Trim();
            }
        }
        string objectName = item.gameObject.name.Replace("_", " ").Replace("-", " ").Trim();
        return string.IsNullOrWhiteSpace(objectName) ? "دکمه بدون عنوان" : objectName;
    }

    private void ActivateCurrent()
    {
        if (!enabledMode) return;
        RefreshControls();
        if (selectedIndex < 0 || selectedIndex >= controls.Count)
        {
            Say("ابتدا یک گزینه انتخاب کنید");
            return;
        }
        Selectable item = controls[selectedIndex];
        if (item is Button button) button.onClick.Invoke();
        else if (item is Toggle toggle) toggle.isOn = !toggle.isOn;
        else if (item is InputField input) input.ActivateInputField();
        else
        {
            // Supports TMP_InputField without a compile-time TMP dependency.
            PropertyInfo activate = item.GetType().GetProperty("isOn");
            if (activate != null && activate.PropertyType == typeof(bool) && activate.CanWrite)
            {
                bool current = (bool)activate.GetValue(item, null);
                activate.SetValue(item, !current, null);
            }
            Say(GetAccessibleLabel(item));
        }
    }

    private void InitTts()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        try
        {
            using (AndroidJavaClass player = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
            using (AndroidJavaObject activity = player.GetStatic<AndroidJavaObject>("currentActivity"))
            {
                tts = new AndroidJavaObject("android.speech.tts.TextToSpeech", activity, new TtsInitListener(this));
            }
        }
        catch (Exception e) { Debug.LogWarning("Accessibility TTS init failed: " + e.Message); }
#endif
    }

    private void Say(string message)
    {
        if (string.IsNullOrWhiteSpace(message)) return;
#if UNITY_ANDROID && !UNITY_EDITOR
        if (tts != null && ttsReady)
        {
            try { tts.Call<int>("speak", message, 0, null, "unity_accessibility"); }
            catch (Exception e) { Debug.LogWarning("Accessibility TTS speak failed: " + e.Message); }
            return;
        }
#endif
        Debug.Log("[Accessibility] " + message);
    }

#if UNITY_ANDROID && !UNITY_EDITOR
    private sealed class TtsInitListener : AndroidJavaProxy
    {
        private readonly AccessibleUiNavigator owner;
        public TtsInitListener(AccessibleUiNavigator owner)
            : base("android.speech.tts.TextToSpeech$OnInitListener") { this.owner = owner; }
        public void onInit(int status)
        {
            owner.ttsReady = status == 0;
            if (owner.ttsReady) owner.Say("پشتیبانی صوتی آماده است");
        }
    }
#endif
}
