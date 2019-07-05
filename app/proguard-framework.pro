-dontwarn com.pekingese.pagestack.framework.view.**

 -keepclassmembers class * extends com.pekingese.pagestack.framework.page.BasePage {
     public <init> (...);
 }

 -keepclassmembers class * extends com.redefine.welike.frameworkmvvm.ViewModel {
      public <init> (...);
 }