package org.techconnect.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.techconnect.model.FlowChart;
import org.techconnect.model.User;
import org.techconnect.model.session.Session;

/**
 * Created by phani on 1/8/17.
 */

public class FirebaseEvents {

    public static void logAppOpen(Context c) {
        FirebaseAnalytics.getInstance(c).logEvent(FirebaseAnalytics.Event.APP_OPEN, null);
    }

    public static void logViewGuide(Context c, FlowChart flowChart) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, flowChart.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, flowChart.getName());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "guides");
        FirebaseAnalytics.getInstance(c).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    public static void logTutorialBegin(Context c) {
        FirebaseAnalytics.getInstance(c).logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null);
    }

    public static void logTutorialFinish(Context c) {
        FirebaseAnalytics.getInstance(c).logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null);
    }

    public static void logTutorialSkip(Context c) {
        FirebaseAnalytics.getInstance(c).logEvent("tutorial_skip", null);
    }

    public static void logSignin(Context c) {
        FirebaseAnalytics.getInstance(c).logEvent(FirebaseAnalytics.Event.LOGIN, null);
    }

    public static void logStartSession(Context c, Session session) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, session.getFlowchart().getName());
        FirebaseAnalytics.getInstance(c).logEvent("session_begin", bundle);
    }

    public static void logEndSessionEarly(Context c, Session session) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, session.getFlowchart().getName());
        FirebaseAnalytics.getInstance(c).logEvent("session_end_early", bundle);
    }

    public static void logEndSessionEarlyNoSave(Context c, Session session) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, session.getFlowchart().getName());
        bundle.putStringArrayList("info",session.getSessionInfo());
        bundle.putString("steps",session.getStepsCompleted());
        FirebaseAnalytics.getInstance(c).logEvent("session_end_early_nosave", bundle);
    }

    public static void logEndSessionEarlyNoSaveStub(Context c, Session session) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, session.getFlowchart().getName());
        bundle.putStringArrayList("info",session.getSessionInfo());
        bundle.putString("steps",session.getStepsCompleted());
        FirebaseAnalytics.getInstance(c).logEvent("session_end_early_nosave_stub", bundle);
    }

    public static void logSessionPausedFull(Context c, Session session) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, session.getFlowchart().getName());
        bundle.putStringArrayList("info",session.getSessionInfo());
        bundle.putString("steps",session.getStepsCompleted());
        FirebaseAnalytics.getInstance(c).logEvent("session_paused", bundle);
    }

    //If the session is resumed, don't need to repeat info
    public static void logSessionPausedStub(Context c, Session session) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, session.getFlowchart().getName());
        bundle.putStringArrayList("info",session.getSessionInfo());
        bundle.putString("steps",session.getStepsCompleted());
        FirebaseAnalytics.getInstance(c).logEvent("session_paused_stub", bundle);
    }

    public static void logSessionCompleteStub(Context c, Session session) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, session.getFlowchart().getName());
        bundle.putStringArrayList("info",session.getSessionInfo());
        bundle.putString("steps",session.getStepsCompleted());
        FirebaseAnalytics.getInstance(c).logEvent("session_complete_stub", bundle);
    }

    public static void logSessionCompleteFull(Context c, Session session) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - session.getCreatedDate();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, session.getFlowchart().getName());
        bundle.putStringArrayList("info",session.getSessionInfo());
        bundle.putString("steps",session.getStepsCompleted());
        FirebaseAnalytics.getInstance(c).logEvent("session_complete", bundle);
    }

    public static void logContactExpertFromFragment(Context c, Session s) {
        Bundle bundle = null;
        if (s != null) {
            bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, s.getId());
            bundle.putStringArrayList("info",s.getSessionInfo());
        }
        FirebaseAnalytics.getInstance(c).logEvent("contact_expert_fragment",bundle);
    }

    public static void logContactExpertFromHistory(Context c, Session s) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, s.getId());
        bundle.putStringArrayList("info",s.getSessionInfo());
        FirebaseAnalytics.getInstance(c).logEvent("contact_expert_history", bundle);
    }

    public static void logSessionInfoEdited(Context c, Session session) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, session.getFlowchart().getName());
        bundle.putStringArrayList("info", session.getSessionInfo());
        FirebaseAnalytics.getInstance(c).logEvent("session_complete", bundle);
    }


    public static void logViewProfile(Context c, User user) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, user.get_id());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "user");
        FirebaseAnalytics.getInstance(c).logEvent("view_profile", bundle);
    }

    public static void logEmailClicked(Context c, User user) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, user.get_id());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "user");
        FirebaseAnalytics.getInstance(c).logEvent("clicked_email", bundle);
    }

    public static void logRegistrationFailed(Context c) {
        FirebaseAnalytics.getInstance(c).logEvent("register_fail", null);
    }

    public static void logRegistrationSuccess(Context c) {
        FirebaseAnalytics.getInstance(c).logEvent(FirebaseAnalytics.Event.SIGN_UP, null);
    }

    public static void logPostComment(Context c, String id, String name) {
        Bundle fbBundle = new Bundle();
        fbBundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        fbBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        FirebaseAnalytics.getInstance(c).logEvent("post_comment", fbBundle);
    }

    public static void logDownloadGuide(Context c, FlowChart chart) {
        Bundle fbBundle = new Bundle();
        fbBundle.putString(FirebaseAnalytics.Param.ITEM_ID, chart.getId());
        fbBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, chart.getName());
        fbBundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "guides");
        FirebaseAnalytics.getInstance(c).logEvent("download_guide", fbBundle);
    }

    public static void logDeleteSession(Context c, Session session) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, session.getFlowchart().getName());
        bundle.putStringArrayList("info",session.getSessionInfo());
        bundle.putString("steps",session.getStepsCompleted());
        FirebaseAnalytics.getInstance(c).logEvent("session_deleted", bundle);
    }

    public static void logResumeSession(Context c, Session session) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, session.getFlowchart().getName());
        FirebaseAnalytics.getInstance(c).logEvent("session_resumed", bundle);
    }

    public static void logGuideFeedback(Context c, Session session, String expFeedback, String contactFeedback, String comments) {
        Bundle fbBundle = new Bundle();
        fbBundle.putLong("session_created", session.getCreatedDate());
        if (session.hasChart()) {
            fbBundle.putString(FirebaseAnalytics.Param.ITEM_ID, session.getFlowchart().getId());
        }
        fbBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, session.getDeviceName());
        fbBundle.putString("comments", comments);
        fbBundle.putString("experienceOpt", expFeedback);
        fbBundle.putString("contactOpt", contactFeedback);
        FirebaseAnalytics.getInstance(c).logEvent("guide_feedback", fbBundle);
    }

    public static void logViewResource(Context context, String parentChart, String att) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,att);
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,parentChart);
        FirebaseAnalytics.getInstance(context).logEvent("view_resource", bundle);

    }
}
