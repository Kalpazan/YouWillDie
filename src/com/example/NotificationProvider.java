package com.example;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationProvider {

	private static final String LAST_NOTIFICATION_NUM = "lastNotificationNum";
	private static final String STORE_NAME = "huj";
	
	private NotificationTemplate[] notifications = {
			new NotificationTemplate("Click to see tits!", "(*)(.)", "c====3", R.drawable.icon_2),
			new NotificationTemplate("Hi man!", "Купи 61 консерву с мертвой рыбой, тебе они будут необходимы при межгалактическом перемещении", "c=3", R.drawable.icon_3),
			new NotificationTemplate("Hi man!", "Не забудь чаще звонить своим друзьям и говорить с ними. Нужно больше общения (тут ссылка на фейсбук)", "c=3", R.drawable.icon_4),
			new NotificationTemplate("Hi man!", "Позвони родителям, они всегда ждут этого. Делай это не реже раза в два дня", "c=3", R.drawable.icon_5),
			new NotificationTemplate("Hi man!", "Составь список неободимой растительности и животного мира, которые ты хочешь видеть на новой планете. (тут нужно придумать какой-то ресурс общаговский, типа evernote, <a href='drive.google.com'>googleDoc</a>, or Dropbox.com)", "c=3", R.drawable.icon_1),
			new NotificationTemplate("Hi man!", "Если ты еще не умеешь готовить - приготовь борщ! (тут мы будем указывать ссылку на рецепт с сайта, администрация которого заплатит нам деньги за рекламу) ", "c=3", R.drawable.icon_2),
			new NotificationTemplate("Hi man!", "Пора учить азбуку морзе - нужно же будет как-то поддерживать связь с соседями ", "c=3", R.drawable.icon_3),
			new NotificationTemplate("Hi man!", "Срочно иди и займись сексом - пора уже думать о восстановлении популяции людей."+
	"И помни: чем больше детей у тебя будет, тем больше полей сможет вспахать твоя семья!", "c=3", R.drawable.icon_4),
			new NotificationTemplate("Hi man!", "Купи саперную лопату! Тебе она подадобится, чтобы что-то откапывать и закапывать.", "c=3", R.drawable.icon_5),
			new NotificationTemplate("Hi man!", "Приобрети там-там для общения с дальними соседями", "c=3", R.drawable.icon_1),
			new NotificationTemplate("Hi man!", "Ты никогда не был в гей-баре. Скоро конец света. Ты отлично держишься, продолжай в том же духе", "c=3", R.drawable.icon_2),
			new NotificationTemplate("Hi man!", "Если ты хочешь, чтобы у тебя были домашние животные, налови тараканов и пауков", "c=3", R.drawable.icon_3),
	};
	
	private Context context;
	
	
	public NotificationProvider(Context context) {
		this.context = context;
	}

	public NotificationTemplate[] getNotifications() {
		return notifications;
	}

	public int getPreviousNotificationsCount() {
		return notifications.length;
	}


	public List<NotificationTemplate> getPreviousNotifications() {
		return Arrays.asList(notifications);
	}

	public void updateLastNotificationNumber(int number) {
		// We need an Editor object to make preference changes.
	    // All objects are from android.context.Context
	    SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt(LAST_NOTIFICATION_NUM, number);

	    // Commit the edits!
	    editor.commit();
	}
	
	public int getLastNotificationNumber() {
	    SharedPreferences settings = context.getSharedPreferences(STORE_NAME, 0);
	    return settings.getInt(LAST_NOTIFICATION_NUM, -1);
	}

	public NotificationTemplate getNotification(int i) {
		return notifications[i];
	}
	
}
