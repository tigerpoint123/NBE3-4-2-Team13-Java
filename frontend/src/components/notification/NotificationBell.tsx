import { Bell } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useState, useEffect } from "react";
import { notificationService } from "@/services/notificationService";

// 1. 인터페이스 수정
interface Notification {
  id: number;           // userId -> id로 변경
  userId: string;       // number -> string으로 변경
  title: string;
  content: string;
  createdAt: string;
  read: boolean;
  type?: string;        // 선택적 필드로 추가
  targetId?: number;    // 선택적 필드로 추가
}

export function NotificationBell() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isLoading, setIsLoading] = useState(false);

  // SSE 리스너 추가
  useEffect(() => {
    // 알림 수신 시 처리할 함수
    const handleNewNotification = (notification: Notification) => {
      setNotifications(prev => [notification, ...prev]);
      setUnreadCount(prev => prev + 1);
    };

    // NotificationService에 리스너 등록
    notificationService.addNotificationListener(handleNewNotification);
    
    // SSE 연결 시작
    notificationService.connect();

    // 컴포넌트 언마운트 시 정리
    return () => {
      notificationService.removeNotificationListener(handleNewNotification);
      notificationService.disconnect();
    };
  }, []);


  // 초기 알림 목록 로드
  useEffect(() => {
    fetchNotifications();
  }, []);

  // 알림 목록 조회 함수
  const fetchNotifications = async () => {
    try {
      setIsLoading(true);
      const token = localStorage.getItem('accessToken');
      
      if (!token) return;

      // userId 파라미터 제거 - 서버에서 토큰으로 사용자 식별
      const response = await fetch(`http://localhost:8080/api/v1/notifications`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        }
      });

      if (!response.ok) {
        throw new Error('알림 목록 조회 실패');
      }

      const data = await response.json();
      console.log('받아온 알림 데이터:', data); // 디버깅용 로그

      if (data.isSuccess) {
        setNotifications(data.data);
        setUnreadCount(data.data.filter((n: Notification) => !n.read).length);
      }
    } catch (error) {
      console.error('알림 목록 조회 중 오류:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // 3. 읽음 처리 함수 수정 - notificationId 사용
  const handleMarkAsRead = async (notificationId: number) => {
    try {
      const token = localStorage.getItem('accessToken');
      await fetch(`http://localhost:8080/api/v1/notifications/${notificationId}/read`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${token}`,
        }
      });

      setNotifications(notifications.map(n =>
        n.id === notificationId ? { ...n, read: true } : n  // userId -> id로 변경
      ));
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('알림 읽음 처리 중 오류:', error);
    }
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild onClick={fetchNotifications}>
        <Button variant="ghost" size="icon" className="relative">
          <Bell className="h-5 w-5" />
          {unreadCount > 0 && (
            <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
              {unreadCount}
            </span>
          )}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-80">
      {isLoading ? (
        <div className="p-4 text-center text-muted-foreground">
          알림 로딩중...
        </div>
      ) : notifications.length === 0 ? (
        <div className="p-4 text-center text-muted-foreground">
          새로운 알림이 없습니다
        </div>
      ) : (
        notifications.map((notification) => (
          <DropdownMenuItem
            key={notification.id}  // userId -> id로 변경
            className={`p-4 cursor-pointer ${!notification.read ? 'bg-muted/50' : ''}`}
            onClick={() => handleMarkAsRead(notification.id)}  // userId -> id로 변경
          >
            <div>
              <div className="font-semibold">{notification.title}</div>
              <div className="text-sm text-muted-foreground">{notification.content}</div>
              <div className="text-xs text-muted-foreground mt-1">
                {new Date(notification.createdAt).toLocaleString()}
              </div>
            </div>
          </DropdownMenuItem>
        ))
      )}
    </DropdownMenuContent>
    </DropdownMenu>
  );
} 