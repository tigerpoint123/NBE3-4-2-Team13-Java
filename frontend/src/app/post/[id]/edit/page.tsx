import { cookies } from "next/headers";

import client from "@/lib/backend/client";

import ClientPage from "./ClientPage";

async function getPost(id: string) {
  try {
    const res = await client.GET("/api/v1/posts/{id}", {
      params: {
        path: {
          id: parseInt(id),
        },
      },
      headers: {
        cookie: (await cookies()).toString(),
      },
    });

    return res.data ?? null;
  } catch (error) {
    console.error("게시글 조회 중 오류 발생:", error);
    return null;
  }
}

export default async function Page({ params }: { params: { id: string } }) {
  const { id } = await params;
  const post = await getPost(id);

  if (!post) {
    return <div>게시글을 찾을 수 없습니다.</div>;
  }

  return <ClientPage post={post} />;
}
