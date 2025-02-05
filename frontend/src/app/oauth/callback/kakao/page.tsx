'use client';

import { useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';

export default function KakaoCallback() {
    const router = useRouter();
    const isCallbackProcessed = useRef(false);

    useEffect(() => {
        const handleKakaoCallback = async () => {
            // 이미 처리된 경우 중복 실행 방지
            if (isCallbackProcessed.current) return;
            
            try {
                const code = new URL(window.location.href).searchParams.get('code');
                
                if (!code) {
                    throw new Error('인증 코드가 없습니다.');
                }

                isCallbackProcessed.current = true;  // 처리 시작 표시
                
                const response = await fetch(`http://localhost:8080/api/v1/members/kakao/callback?code=${code}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json',
                    },
                    credentials: 'include',
                    mode: 'cors'
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.json();
                console.log('로그인 응답 데이터:', data);  // 응답 데이터 확인용 로그
                
                if (data.accessToken) {  // accessToken이 있는 경우에만 저장
                    // 응답 데이터를 localStorage에 저장
                    Object.entries(data).forEach(([key, value]) => {
                        localStorage.setItem(key, String(value));
                    });
                    router.push('/');  // 성공시에만 리다이렉트
                } else {
                    throw new Error('액세스 토큰이 없습니다.');
                }
                
            } catch (error) {
                console.error('카카오 로그인 처리 중 오류:', error);
                isCallbackProcessed.current = false;  // 에러 발생시 재시도 가능하도록
                router.push('/');  // 에러시 로그인 페이지로 이동
            }
        };

        handleKakaoCallback();

        return () => {
            isCallbackProcessed.current = false;
        };
    }, [router]);

    return (
        <div className="flex justify-center items-center min-h-screen">
            <div className="text-center">
                <h2 className="text-xl font-bold mb-4">로그인 처리 중...</h2>
                <p>잠시만 기다려주세요.</p>
            </div>
        </div>
    );
}