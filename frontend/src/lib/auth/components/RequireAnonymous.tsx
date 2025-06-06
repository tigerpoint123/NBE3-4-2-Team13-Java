"use client";

import { Button } from "@/components/ui/button";
import { LoginMemberContext } from "@/stores/auth/LoginMember";
import { use } from "react";
import Link from "next/link";

export default function RequireAnonymous({ children }: { children: React.ReactNode }) {

    const { isLogin } = use(LoginMemberContext);

    if (isLogin) {
        return  (
            <div className="flex-1 flex justify-center items-center">
                <div className="text-muted-foreground">
                    해당 페이지는 로그아웃 후 이용할 수 있습니다.
                </div>
                <div className="mt-2 flex justify-center">
                    <Button variant="link" asChild>
                    <Link href="/">메인으로 돌아가기</Link>
                    </Button>
                </div>
            </div>
        );
    }

    return <>{children}</>;
}

