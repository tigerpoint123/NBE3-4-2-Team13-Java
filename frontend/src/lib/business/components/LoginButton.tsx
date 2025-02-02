"use client";

import { Button } from "@/components/ui/button";
import { LogIn } from "lucide-react";

export default function LoginButton({
  variant,
  className,
  text,
  icon,
}: {
  variant?:
    | "link"
    | "default"
    | "destructive"
    | "outline"
    | "secondary"
    | "ghost"
    | null
    | undefined;
  className?: string;
  text?: string | boolean;
  icon?: React.ReactNode;
}) {
  const socialLoginForKakaoUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${process.env.NEXT_PUBLIC_KAKAO_CLIENT_ID}`;
  const redirectUrlAfterSocialLogin = `http://localhost:8080/api/v1/members/kakao/callback`;

  if (!variant) variant = "link";
  if (typeof text === "boolean") text = "로그인";

  return (
    <Button variant={variant} className={className} asChild>
      <a
        href={`${socialLoginForKakaoUrl}&redirect_uri=${redirectUrlAfterSocialLogin}&response_type=code`}
      >
        {icon || <LogIn />}
        {text && <span>{text}</span>}
      </a>
    </Button>
  );
}
