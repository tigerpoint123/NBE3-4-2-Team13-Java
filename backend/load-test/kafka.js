import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');
const messageReceivedRate = new Rate('messages_received');

export const options = {
  vus: 30,              // 10명의 가상 유저
  duration: '30s',      // 40초 동안 테스트
  thresholds: {
    'errors': ['rate<0.1'],           // 에러율 10% 미만
    'http_req_duration': ['p(95)<500'], // 95%의 요청이 500ms 이내
    'messages_received': ['rate>0.9'],  // 90% 이상의 메시지 수신 성공
  },
};

export default function () {
  const BASE_URL = 'http://host.docker.internal:8080/api/v1/notifications';
  
  // 로그인하여 토큰 얻기
  const loginResponse = http.post('http://host.docker.internal:8080/api/v1/members/login', 
    JSON.stringify({
      username: 'user100',
      password: 'user100'
    }), 
    {
      headers: {
        'Content-Type': 'application/json'
      }
    }
  );

  check(loginResponse, {
    'login successful': (r) => r.status === 200
  });

  if (loginResponse.status === 200) {
    const responseData = JSON.parse(loginResponse.body);
    const token = responseData.data.accessToken;

    // SSE 연결 테스트
    const response = http.get(`${BASE_URL}/subscribe`, {
      headers: {
        'Accept': 'text/event-stream',
        'Authorization': `Bearer ${token}`
      },
      timeout: '35s'  // SSE 연결 타임아웃 설정
    });

    // 카프카를 통한 메시지 발행
    const sendResponse = http.post(`${BASE_URL}/send`, 
      JSON.stringify({
        userId: 1,
        title: '그룹 초대',
        content: '테스트 그룹에 초대되었습니다.',
        type: 'GROUP_INVITE',
        id: 1
      }),
      {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      }
    );

    check(sendResponse, {
      'kafka message sent successful': (r) => r.status === 200
    });

    check(response, {
      'status is 200': (r) => r.status === 200,
      'has event stream': (r) => {
        const contentType = r.headers['Content-Type'];
        return contentType && contentType.includes('text/event-stream');
      },
      'received message': (r) => {
        const hasMessage = r.body.includes('data:');
        if (hasMessage) {
          messageReceivedRate.add(1);
        }
        return hasMessage;
      }
    }) || errorRate.add(1);
  } else {
    errorRate.add(1);
  }

  sleep(1);
} 