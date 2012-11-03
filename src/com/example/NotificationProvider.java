package com.example;

import java.util.Arrays;
import java.util.List;

import android.content.res.Resources;
import android.content.res.TypedArray;

public class NotificationProvider {

//	private NotificationTemplate[] notifications = {
//			new NotificationTemplate("Рог", "Привыкай к единорогам: их у нас много. Многорог - всего один", "кстати", R.drawable.icon_tongue),
//			new NotificationTemplate("Стой!", "Прямо сейчас подними голову и посмотри по сторонам. Постарайся запомнить свою планету перед отлетом. Можешь даже сделать несколько фотографий на память", "фотко", R.drawable.icon_tongue),
//			new NotificationTemplate("Радость", "Апокалипсис... Ну зато тебе не нужен айфон. Серьезно: наше приложение на нем не работает", ":)", R.drawable.icon_tongue),
//			new NotificationTemplate("Click to see tits!", "(*)(.)", "c====3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "Купи 61 консерву с мертвой рыбой, тебе они будут необходимы при межгалактическом перемещении", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "Не забудь чаще звонить своим друзьям и говорить с ними. Нужно больше общения (тут ссылка на фейсбук)", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "Позвони родителям, они всегда ждут этого. Делай это не реже раза в два дня", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "Составь список неободимой растительности и животного мира, которые ты хочешь видеть на новой планете. (тут нужно придумать какой-то ресурс общаговский, типа evernote, <a href='drive.google.com'>googleDoc</a>, or Dropbox.com)", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "Если ты еще не умеешь готовить - приготовь борщ! (тут мы будем указывать ссылку на рецепт с сайта, администрация которого заплатит нам деньги за рекламу) ", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "Пора учить азбуку морзе - нужно же будет как-то поддерживать связь с соседями ", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "Срочно иди и займись сексом - пора уже думать о восстановлении популяции людей. И помни: чем больше детей у тебя будет, тем больше полей сможет вспахать твоя семья!", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "Купи саперную лопату! Тебе она подадобится, чтобы что-то откапывать и закапывать.", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "Приобрети там-там для общения с дальними соседями", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "Ты никогда не был в гей-баре. Скоро конец света. Ты отлично держишься, продолжай в том же духе", "c=3", R.drawable.icon_tongue),
//			new NotificationTemplate("Hi man!", "Если ты хочешь, чтобы у тебя были домашние животные, налови тараканов и пауков", "c=3", R.drawable.icon_tongue),
//	};
	
	private NotificationTemplate[] notifications;
	
	public NotificationProvider(Resources resources) {
		String[] messages = resources.getStringArray(R.array.messages);
		TypedArray imgs = resources.obtainTypedArray(R.array.icons);
		
		notifications = new NotificationTemplate[messages.length];
		
		for (int i = 0; i < messages.length; i++) {
			String[] strings = messages[i].split("\\|");
			int icon = imgs.getResourceId(i, -1);
			if (strings.length == 2) {
				notifications[i] = new NotificationTemplate(strings[0], strings[1], "instruction for today", icon);
			} else {
				notifications[i] = new NotificationTemplate(strings[0].split(" ")[0], strings[0], "instruction for today", icon);
			}
		}
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

	public boolean hasNotificationWithNumber(int number) {
		return number < notifications.length && number >= 0;
	}
	
	public NotificationTemplate getNotification(int number) {
		if (number < notifications.length) {
			return notifications[number];
		}
		return null;
	}
	
}
