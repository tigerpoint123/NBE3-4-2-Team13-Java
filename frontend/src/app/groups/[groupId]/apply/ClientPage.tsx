"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";

interface ClientPageProps {
    groupId: string;
}

const ClientPage = ({ groupId }: ClientPageProps) => {
    const router = useRouter();
    const [formData, setFormData] = useState({ reason: "" });
    const [token, setToken] = useState<string | undefined>(undefined);
    const [loading, setLoading] = useState(true);

    // 클라이언트에서 localStorage 접근하도록 useEffect 사용
    useEffect(() => {
        if (typeof window !== "undefined") {
            const storedToken = localStorage.getItem("token");
            setToken(storedToken || null);
        }
        setLoading(false);
    }, []);

    // 입력값 변경 핸들러
    const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // 신청 폼 제출 핸들러
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // useEffect 실행이 끝나기 전에 submit되었을 경우, 다시 localStorage에서 확인
        const currentToken = token ?? localStorage.getItem("token");

        if (!currentToken) {
            alert("로그인이 필요합니다.");
            router.push("/login");
            return;
        }

        try {
            const response = await fetch(`/api/v1/groups/${groupId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${currentToken}`,
                },
                credentials: "include", // 인증 정보 포함 (CORS 문제 방지)
                body: JSON.stringify(formData),
            });

            const contentType = response.headers.get("content-type");

            console.log("Response Status:", response.status);
            console.log("Response Content-Type:", contentType);

            if (!response.ok) {
                const errorMessage = contentType?.includes("application/json")
                    ? (await response.json()).message
                    : await response.text(); // HTML 에러 페이지일 경우

                throw new Error(errorMessage || "신청에 실패했습니다.");
            }

            const data = await response.json();
            alert(data.message);
            router.push(`/groups/${groupId}`);
        } catch (error) {
            console.error("Error submitting application:", error);
            alert(`오류 발생: ${error.message || "알 수 없는 오류"}`);
        }
    };

    if (loading) {
        return <p className="text-center text-gray-500 dark:text-gray-400">로딩 중...</p>;
    }

    return (
        <div className="max-w-4xl mx-auto p-16 bg-white dark:bg-gray-900 rounded-xl shadow-xl">
            <h2 className="text-3xl font-bold mb-10 text-gray-900 dark:text-gray-100">모임 신청</h2>
            <form onSubmit={handleSubmit} className="space-y-8">
                <div>
                    <label className="block text-gray-700 dark:text-gray-300 text-lg font-bold mb-3">
                        자기 소개
                    </label>
                    <textarea
                        name="reason"
                        value={formData.reason}
                        onChange={handleChange}
                        className="w-full h-52 px-5 py-4 border rounded-xl text-lg text-gray-900 dark:text-gray-100 bg-white dark:bg-gray-800 border-gray-300 dark:border-gray-700"
                        placeholder="성별, 나이 등 본인의 대한 소개를 자유롭게 작성해주세요."
                        required
                    />
                </div>
                <div className="flex">
                    <button
                        type="submit"
                        className="px-6 py-3 border border-gray-700 dark:border-gray-400 text-gray-700 dark:text-gray-300 rounded-lg bg-white dark:bg-gray-800 hover:bg-gray-700 hover:text-white dark:hover:bg-gray-600 transition"
                    >
                        신청하기
                    </button>
                </div>
            </form>
        </div>
    );
};

export default ClientPage;
