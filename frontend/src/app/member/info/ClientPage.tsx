"use client";
import { LoginMemberContext } from "@/stores/auth/LoginMember";
import { use, useEffect } from "react";

export default function ClientPage() {
    // 날짜 포맷팅 함수
    const formatDate = (dateString: string) => {
        if (!dateString) return "정보 없음";
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    // 권한 표시 포맷팅
    // const formatAuthorities = (authorities: Array<{authority: string}>) => {
    //     return authorities.map(auth => {
    //         switch(auth.authority) {
    //             case 'ROLE_USER':
    //                 return '일반 회원';
    //             case 'ROLE_ADMIN':
    //                 return '관리자';
    //             default:
    //                 return auth.authority;
    //         }
    //     }).join(', ');
    // };

    const { loginMember } = use(LoginMemberContext);

    return (
        <div className="container mx-auto p-4 max-w-2xl">
            <div className="bg-white rounded-lg shadow-lg p-6">
                <div className="mb-6">
                    <h2 className="text-2xl font-bold">회원 정보</h2>
                </div>
                <div className="space-y-4">
                    <div className="grid grid-cols-3 gap-4">
                        <div className="font-semibold">닉네임</div>
                        <div className="col-span-2">{loginMember.nickname}</div>

                        <div className="font-semibold">회원 ID</div>
                        <div className="col-span-2">{loginMember.username}</div>

                        <div className="font-semibold">비밀번호</div>
                        <div className="col-span-2">{loginMember.password}</div>

                        <div className="font-semibold">가입일</div>
                        <div className="col-span-2">{formatDate(loginMember.createdAt)}</div>

                        {/* <div className="font-semibold">회원 권한</div>
                        <div className="col-span-2">{formatAuthorities(loginMember.role.map(auth => ({authority: auth})))}</div> */}

                        <div className="font-semibold">가입 방식</div>
                        <div className="col-span-2">{loginMember.provider}</div>
                    </div>
                </div>
            </div>

        </div>
    );
}