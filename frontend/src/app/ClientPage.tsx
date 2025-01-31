"use client";

import KaKaoLoginButton from "@/lib/business/components/KaKaoLoginButton";

import { useGlobalLoginMember } from "@/stores/auth/loginMember";
import { useEffect } from "react";

export default function ClientPage() {
  const { isLogin, loginMember, checkLoginStatus } = useGlobalLoginMember();

  useEffect(() => {
    // 페이지 로드시 로그인 상태 확인
    checkLoginStatus();
  }, [isLogin]);

  return (
    <div className="flex-1 flex justify-center items-center">
      {!isLogin && <KaKaoLoginButton text />}
      {isLogin && <div>{loginMember.nickname}님 환영합니다.</div>}
    </div>
  );
}
