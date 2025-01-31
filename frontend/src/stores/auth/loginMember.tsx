"use client";

import { createContext, use, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import client from "@/lib/backend/client";

type Member = {
  id: number;
  nickname: string;
  profileImgUrl: string | null;  // null도 허용하도록 수정
  createDate: string;  // 추가
};

export const LoginMemberContext = createContext<{
  loginMember: Member;
  setLoginMember: (member: Member) => void;
  isLoginMemberPending: boolean;
  isLogin: boolean;
  isAdmin: boolean;
  logout: (callback: () => void) => void;
  logoutAndHome: () => void;
  isAdminPage: boolean;
  isUserPage: boolean;
  checkLoginStatus: () => Promise<void>;
}>({
  loginMember: createEmptyMember(),
  setLoginMember: () => {},
  isLoginMemberPending: true,
  isLogin: false,
  isAdmin: false,
  logout: () => {},
  logoutAndHome: () => {},
  isAdminPage: false,
  isUserPage: false,
  checkLoginStatus: async () => {},
});

function createEmptyMember(): Member {
  return {
    id: 0,
    nickname: "",
    profileImgUrl: "",
    createDate: new Date().toISOString(),  // 추가
  };
}

export function useLoginMember() {
  const router = useRouter();
  const pathname = usePathname();

  const [isLoginMemberPending, setLoginMemberPending] = useState(true);
  const [loginMember, _setLoginMember] = useState<Member>(createEmptyMember());

  const removeLoginMember = () => {
    _setLoginMember(createEmptyMember());
    setLoginMemberPending(false);
  };

  const setLoginMember = (member: Member) => {
    _setLoginMember(member);
    setLoginMemberPending(false);
  };

  const setNoLoginMember = () => {
    setLoginMemberPending(false);
  };

  const isLogin = loginMember.id !== 0;
  const isAdmin = loginMember.id === 2;

  const logout = (callback: () => void) => {
    client.DELETE("/api/v1/members/logout").then(() => {
      removeLoginMember();
      callback();
    });
  };

  const logoutAndHome = () => {
    logout(() => router.replace("/"));
  };

  const isAdminPage = pathname.startsWith("/adm");
  const isUserPage = !isAdminPage;

  const checkLoginStatus = async () => {
    try {
      // 쿠키 파싱 함수
      const getAccessTokenFromCookie = () => {
        const cookies = document.cookie;
        const match = cookies.match(/accessToken=([^;]+)/);
        const accessToken = match ? match[1] : null;
        return accessToken;
      };

      const accessToken = getAccessTokenFromCookie();

      if (!accessToken) {
        setNoLoginMember();
        return;
      }

      const response = await fetch("http://localhost:8080/api/v1/members/info", {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${accessToken}`
        },
        credentials: 'include'
      });
      
      if (response.ok) {
        const responseData = await response.json();
        // API 응답 구조에 맞게 수정
        if (responseData.isSuccess && responseData.data) {
          const memberData = {
            id: responseData.data.id,
            nickname: responseData.data.nickname,
            createDate: responseData.data.createDate || "",
            modifyDate: responseData.data.modifyDate || "",
            profileImgUrl: responseData.data.profileImgUrl || "https://placehold.co/80x80"
          };
          setLoginMember(memberData);
        } else {
          setNoLoginMember();
        }
      } else {
        setNoLoginMember();
      }
    } catch (error) {
      console.error('로그인 상태 확인 실패:', error);
      setNoLoginMember();
    }
  };

  return {
    loginMember,
    setLoginMember,
    isLoginMemberPending,
    setNoLoginMember,
    isLogin,
    isAdmin,
    logout,
    logoutAndHome,
    isAdminPage,
    isUserPage,
    checkLoginStatus,
  };
}

export function useGlobalLoginMember() {
  return use(LoginMemberContext);
}