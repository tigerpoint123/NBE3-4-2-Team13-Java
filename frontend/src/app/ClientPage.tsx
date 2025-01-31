"use client";

import KaKaoLoginButton from "@/lib/business/components/KaKaoLoginButton";

import { useGlobalLoginMember } from "@/stores/auth/loginMember";

export default function ClientPage() {
  const { isLogin, loginMember } = useGlobalLoginMember();

  return (
    <div className="flex-1 flex justify-center items-center">
      {!isLogin && <KaKaoLoginButton text />}
      {isLogin && <div>{loginMember.nickname}님 환영합니다.</div>}
    </div>
  );
}
