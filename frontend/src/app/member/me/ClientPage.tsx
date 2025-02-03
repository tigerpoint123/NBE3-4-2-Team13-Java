"use client";
import { LoginMemberContext } from "@/stores/auth/LoginMember";
import { use } from "react";

export default function ClientPage() {
    const { loginMember } = use(LoginMemberContext);

    return (
        <div className="flex-1 flex justify-center items-center">
            별명 : {loginMember.nickname}
        </div>
    );
}