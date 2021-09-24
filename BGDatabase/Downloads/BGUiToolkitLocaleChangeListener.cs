using UnityEngine;

namespace BansheeGz.BGDatabase
{
    //This class listen to locale changed event and rebind all toolkit binders
    public class BGUiToolkitLocaleChangeListener : MonoBehaviour, BGAddonLocalization.LocaleChangeReceiver
    {
        private static BGUiToolkitLocaleChangeListener instance;
        void Awake()
        {
            if (instance != null) return;
            instance = this;
            DontDestroyOnLoad(gameObject);
        }

        public void LocaleChanged()
        {
            var binders = Resources.FindObjectsOfTypeAll<BGDataBinderUiToolkitGo>();
            foreach (var binder in binders) binder.Bind();
        }
    }
    
}
