package com.redefine.welike.business.assignment.ui.contract;

import com.pekingese.pagestack.framework.page.BasePage;
import com.redefine.foundation.mvp.IBasePresenter;
import com.redefine.welike.business.assignment.ui.presenter.AssignmentNotifyPresenter;

public interface IAssignmentNotifyContract {

    interface IAssignmentNotifyPresenter extends IBasePresenter {
        void onPageStateChange(BasePage basePage, int oldPageState, int newPageState);
    }

    class AssignmentFactory {

        public static IAssignmentNotifyPresenter createNotifyPresenter() {
            return new AssignmentNotifyPresenter();
        }
    }
}
