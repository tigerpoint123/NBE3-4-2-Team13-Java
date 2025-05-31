import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');
const messageReceivedRate = new Rate('messages_received');

export const options = {
  stages: [
    { duration: '5s', target: 10 },   // 5초 동안 10명의 사용자로 증가
    { duration: '30s', target: 10 },  // 30초 동안 10명의 사용자 유지
    { duration: '5s', target: 0 },    // 5초 동안 사용자 수 감소
  ],
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