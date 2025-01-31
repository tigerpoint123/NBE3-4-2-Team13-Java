"use client";

import Image from "next/image";

import { useGlobalLoginMember } from "@/stores/auth/loginMember";

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

export default function ClientPage() {
  const { loginMember } = useGlobalLoginMember();

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
              src={loginMember.profileImgUrl}
              alt={loginMember.nickname}
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
