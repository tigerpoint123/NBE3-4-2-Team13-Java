"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import client from "@/lib/backend/client";
import { useToast } from "@/hooks/use-toast";
import { useGlobalLoginMember } from "@/stores/auth/loginMember";
import { components } from "@/lib/backend/apiV1/schema";

type Member = components["schemas"]["MemberDto"];

export default function KakaoCallback() {
  const router = useRouter();
  const { toast } = useToast();
  const { setLoginMember } = useGlobalLoginMember();

  useEffect(() => {
    const code = new URL(window.location.href).searchParams.get('code');
    
    if (code) {
      client.POST("/api/v1/members/kakao/callback", {
        body: { code }
      })
      .then(response => {
        if (response.error) {
          throw new Error(response.error.msg);
        }
        
        const member = response.data.data as Member;
        setLoginMember(member);
        toast({
          title: "카카오 로그인 성공",
        });
        router.replace('/');
      })
      .catch(error => {
        console.error('카카오 로그인 실패:', error);
        toast({
          title: "카카오 로그인 실패",
          description: error.message,
          variant: "destructive",
        });
        router.replace('/');
      });
    }
  }, [router, toast, setLoginMember]);

  return (
    <div className="flex-1 flex justify-center items-center">
      카카오 로그인 처리중...
    </div>
  );
} 