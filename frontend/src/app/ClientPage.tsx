"use client";

import { Button } from "@/components/ui/button";
import { MessageCircle } from "lucide-react";

export default function ClientPage() {
  const socialLoginForKakaoUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${process.env.NEXT_PUBLIC_KAKAO_CLIENT_ID}`;
  const redirectUrlAfterSocialLogin = `http://localhost:8080/api/v1/members/kakao/callback`;

  return (
    <div className="flex-1 flex justify-center items-center">
        <Button variant="outline" asChild>
            <a
            href={`${socialLoginForKakaoUrl}&redirect_uri=${redirectUrlAfterSocialLogin}&response_type=code`}
            >
            <MessageCircle />
                <span className="font-bold">카카오 로그인</span>
            </a>
        </Button>
    </div>
  );
}
