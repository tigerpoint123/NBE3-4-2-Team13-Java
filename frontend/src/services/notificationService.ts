import { EventSourceMessage, fetchEventSource } from '@microsoft/fetch-event-source';

class NotificationService {
  private controller: AbortController | null = null;
  private isConnecting: boolean = false;
  private listeners: ((notification: any) => void)[] = [];

  async connect() {
    if (this.isConnecting) return;
    this.isConnecting = true;

    const token = localStorage.getItem('accessToken');
    if (!token) {
      this.isConnecting = false;
      return;
    }

    try {
      this.disconnect();
      this.controller = new AbortController();

      await fetchEventSource(`http://localhost:8080/api/v1/notifications/subscribe`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
        signal: this.controller.signal,
        onopen: async (response) => {
          console.log('SSE 연결 성공');
          this.isConnecting = false;
        },
        onmessage: (event: EventSourceMessage) => {
          try {
            // 이벤트 타입에 따른 처리
            if (event.event === 'connect') {
              console.log('연결 메시지:', event.data);
              return;
            }

            // 일반 알림 메시지 처리
            const notification = JSON.parse(event.data);
            this.showNotification(notification);
          } catch (error) {
            console.error('알림 처리 중 오류:', error);
          }
        },
        onerror: (error) => {
          console.error('SSE 연결 오류:', error);
          this.isConnecting = false;
          throw error; // 재연결을 위해 에러를 던집니다
        }
      });
    } catch (error) {
      console.error('SSE 연결 시도 중 오류:', error);
      this.isConnecting = false;
      // 재연결 시도
      setTimeout(() => this.connect(), 5000);
    }
  }

  // 알림 목록 조회 메서드 추가
  async getNotifications(userId: string) {
    const token = localStorage.getItem('accessToken');
    if (!token) return [];

    try {
      const response = await fetch(`http://localhost:8080/api/v1/notifications?userId=${userId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      const data = await response.json();
      return data.data;
    } catch (error) {
      console.error('알림 목록 조회 중 오류:', error);
      return [];
    }
  }

  // 알림 읽음 처리 메서드 추가
  async markAsRead(notificationId: number) {
    const token = localStorage.getItem('accessToken');
    if (!token) return;

    try {
      await fetch(`http://localhost:8080/api/v1/notifications/${notificationId}/read`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
    } catch (error) {
      console.error('알림 읽음 처리 중 오류:', error);
    }
  }

  addNotificationListener(listener: (notification: any) => void) {
    this.listeners.push(listener);
  }

  removeNotificationListener(listener: (notification: any) => void) {
    this.listeners = this.listeners.filter(l => l !== listener);
  }

  private showNotification(notification: any) {
    // 브라우저 알림 권한 확인
    if (Notification.permission === 'granted') {
      new Notification(notification.title || '새 알림', {
        body: notification.content,
        icon: '/path/to/icon.png'
      });
    } else if (Notification.permission === 'default') {
      Notification.requestPermission();
    }

    // 리스너들에게 알림
    this.listeners.forEach(listener => listener(notification));
  }

  disconnect() {
    if (this.controller) {
      this.controller.abort();
      this.controller = null;
    }
    this.isConnecting = false;
  }
}

export const notificationService = new NotificationService(); 