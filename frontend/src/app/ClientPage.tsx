"use client";

import { Button } from "@/components/ui/button";
import { LoginMemberContext} from "@/stores/auth/LoginMember"; // LoginMemberContext(by React Context API) 를 통해서 공유되는 전역객체를 가져오기 위해
import { MessageCircle } from "lucide-react";
import { use } from "react"; // React Context API을 통해서 공유되는 전역객체를 가져오기 위해 사용하는 함수 `use` 로드

export default function ClientPage() {
    // frontend/src/app/ClientLayout.tsx 의 `<LoginMemberContext value={loginMemberContextValue}>` 를 통해서 공유된 value 에서 필요한 특정 값들만 가져온다.
    const { isLogin, loginMember } = use(LoginMemberContext);

    const socialLoginForKakaoUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${process.env.NEXT_PUBLIC_KAKAO_CLIENT_ID}`;
    const redirectUrlAfterSocialLogin = `http://localhost:8080/api/v1/members/kakao/callback`;

    return (
        <div className="flex-1 flex justify-center items-center">
            {!isLogin && (
                <Button variant="outline" asChild>
                <a
                    href={`${socialLoginForKakaoUrl}&redirect_uri=${redirectUrlAfterSocialLogin}&response_type=code`}
                >
                    <MessageCircle />
                    <span className="font-bold">카카오 로그인</span>
                </a>
                </Button>
            )}
            {isLogin && <div>{loginMember.nickname}님 환영합니다.</div>}
        </div>
    );
}
