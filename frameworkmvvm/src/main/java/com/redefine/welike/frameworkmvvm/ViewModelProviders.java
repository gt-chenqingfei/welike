package com.redefine.welike.frameworkmvvm;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ViewModelProviders {

    private static Application checkApplication(Activity activity) {
        Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Your activity/fragment is not yet attached to "
                    + "Application. You can't request ViewModel before onCreate call.");
        }
        return application;
    }

    /**
     * Creates a {@link ViewModelProvider}, which retains ViewModels while a scope of given Activity
     * is alive. More detailed explanation is in {@link ViewModel}.
     * <p>
     * It uses {@link ViewModelProvider.AndroidViewModelFactory} to instantiate new ViewModels.
     *
     * @param stackManager an stackManager, in whose scope ViewModels should be retained
     * @return a ViewModelProvider instance
     */
    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull BaseLifecyclePageStackManager stackManager) {
        return of(stackManager, null);
    }

    /**
     * Creates a {@link ViewModelProvider}, which retains ViewModels while a scope of given Activity
     * is alive. More detailed explanation is in {@link ViewModel}.
     * <p>
     * It uses {@link ViewModelProvider.AndroidViewModelFactory} to instantiate new ViewModels.
     *
     * @param baseLifecyclePage a page, in whose scope ViewModels should be retained
     * @return a ViewModelProvider instance
     */
    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull BaseLifecyclePage baseLifecyclePage) {
        return of(baseLifecyclePage, null);
    }

    /**
     * Creates a {@link ViewModelProvider}, which retains ViewModels while a scope of given Activity
     * is alive. More detailed explanation is in {@link ViewModel}.
     * <p>
     * It uses {@link ViewModelProvider.AndroidViewModelFactory} to instantiate new ViewModels.
     *
     * @param baseLifecyclePage a page, in whose scope ViewModels should be retained
     * @return a ViewModelProvider instance
     */
    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull BaseLifecycleFragmentPage baseLifecyclePage) {
        return of(baseLifecyclePage, null);
    }
    /**
     * Creates a {@link ViewModelProvider}, which retains ViewModels while a scope of given
     * {@code fragment} is alive. More detailed explanation is in {@link ViewModel}.
     * <p>
     * It uses the given {@link ViewModelProvider.Factory} to instantiate new ViewModels.
     *
     * @param baseLifecyclePage a page, in whose scope ViewModels should be retained
     * @param factory  a {@code Factory} to instantiate new ViewModels
     * @return a ViewModelProvider instance
     */
    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull BaseLifecycleFragmentPage baseLifecyclePage, @Nullable ViewModelProvider.Factory factory) {
        Application application = checkApplication(baseLifecyclePage.getPageStackManager().getActivity());
        if (factory == null) {
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        return new ViewModelProvider(baseLifecyclePage.getViewModelStore(), factory);
    }

    /**
     * Creates a {@link ViewModelProvider}, which retains ViewModels while a scope of given
     * {@code fragment} is alive. More detailed explanation is in {@link ViewModel}.
     * <p>
     * It uses the given {@link ViewModelProvider.Factory} to instantiate new ViewModels.
     *
     * @param baseLifecyclePage a page, in whose scope ViewModels should be retained
     * @param factory  a {@code Factory} to instantiate new ViewModels
     * @return a ViewModelProvider instance
     */
    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull BaseLifecyclePage baseLifecyclePage, @Nullable ViewModelProvider.Factory factory) {
        Application application = checkApplication(baseLifecyclePage.getPageStackManager().getActivity());
        if (factory == null) {
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        return new ViewModelProvider(baseLifecyclePage.getViewModelStore(), factory);
    }

    /**
     * Creates a {@link ViewModelProvider}, which retains ViewModels while a scope of given
     * {@code fragment} is alive. More detailed explanation is in {@link ViewModel}.
     * <p>
     * It uses the given {@link ViewModelProvider.Factory} to instantiate new ViewModels.
     *
     * @param stackManager a stackManager, in whose scope ViewModels should be retained
     * @param factory  a {@code Factory} to instantiate new ViewModels
     * @return a ViewModelProvider instance
     */
    @NonNull
    @MainThread
    public static ViewModelProvider of(@NonNull BaseLifecyclePageStackManager stackManager, @Nullable ViewModelProvider.Factory factory) {
        Application application = checkApplication(stackManager.getActivity());
        if (factory == null) {
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        return new ViewModelProvider(stackManager.getViewModelStore(), factory);
    }

}
