"use client";

import Image from "next/image";

import { useGlobalLoginMember } from "@/stores/auth/loginMember";
import { useEffect } from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

export default function ClientPage() {
  const { loginMember, checkLoginStatus, isLoginMemberPending } = useGlobalLoginMember();

  useEffect(() => {
    checkLoginStatus();  // 컴포넌트 마운트 시 로그인 상태 체크
  }, [checkLoginStatus]);

  // 로딩 중일 때 표시할 내용
  if (isLoginMemberPending) {
    return (
      <div className="flex-1 flex justify-center items-center">
        <div>로딩 중...</div>
      </div>
    );
  }

  return (
    <div className="flex-1 flex justify-center items-center">
      <Card>
        <CardHeader>
          <CardTitle className="text-center">내 정보</CardTitle>
          <CardDescription className="sr-only">내 정보</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex justify-center items-center gap-4">
            <Image
              src={loginMember.profileImgUrl || "https://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg"}
              alt={`${loginMember.nickname || '사용자'}의 프로필 이미지`}
              width={80}
              height={80}
              quality={100}
              className="rounded-full ring-2 ring-primary/10"
            />
            <div className="text-xl font-medium">{loginMember.nickname}</div>
          </div>
          <div className="my-4">
            <div className="text-sm text-muted-foreground">
              <span>가입날짜 : </span>
              <span>
                {new Date(loginMember.createDate).toLocaleString("ko-KR", {
                  year: "2-digit",
                  month: "2-digit",
                  day: "2-digit",
                  hour: "2-digit",
                  minute: "2-digit",
                })}
              </span>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
