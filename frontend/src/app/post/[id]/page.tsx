import type { Metadata } from "next";

import { cookies } from "next/headers";

import client from "@/lib/backend/client";

import ClientPage from "./ClientPage";

async function getPost(id: string) {
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

  return res;
}

function processMarkdown(input: string) {
  // 1. $$...$$ 또는 ```...``` 내용을 제거
  const cleanedContent = input.replace(
    /(\$\$[\s\S]*?\$\$|```[\s\S]*?```)/g,
    "",
  );

  // 2. 영어, 소괄호, 한글(자음/모음 포함), 띄워쓰기, 줄바꿈 외의 모든 문자 제거
  // 3. 연속된 공백과 줄바꿈을 하나의 공백으로 변경하고 앞뒤 공백 제거
  return cleanedContent
    .replace(/[^a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9().?!\s]/g, "")
    .replace(/\s+/g, " ")
    .trim()
    .slice(0, 157) // 157자까지만 자르기 (... 3글자 포함하여 160자)
    .replace(/(.{157})/, "$1..."); // 157자 이상일 경우에만 ... 추가
}

export async function generateMetadata({
  params,
}: {
  params: { id: string };
}): Promise<Metadata> {
  const { id } = await params;
  const postResponse = await getPost(id);

  if (postResponse.error) {
    return {
      title: postResponse.error.msg,
      description: postResponse.error.msg,
    };
  }

  const post = postResponse.data;

  return {
    title: post.title,
    description: processMarkdown(post.content),
  };
}

export default async function Page({ params }: { params: { id: string } }) {
  const { id } = await params;
  const postResponse = await getPost(id);

  if (postResponse.error) {
    return (
      <div className="flex-1 flex items-center justify-center">
        {postResponse.error.msg}
      </div>
    );
  }

  const post = postResponse.data;

  return <ClientPage post={post} />;
}
