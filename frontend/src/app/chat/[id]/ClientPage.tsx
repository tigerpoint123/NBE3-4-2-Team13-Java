'use client';

import { useEffect, useState, useRef } from 'react';
import { useParams } from 'next/navigation';
import { Users } from 'lucide-react';
import axios from 'axios';

interface Message {
  id: string;
  chatRoomId: number;
  senderId: number;
  senderNickname: string;
  content: string;
  createdAt: number[];
}

interface ChatRoomDetail {
  chatRoomId: number;
  group: {
    groupId: number;
    groupName: string;
    participantCount: number;
  };
  members: {
    memberId: number;
    memberNickname: string;
    groupRole: string;
  }[];
}

export default function ChatRoom() {
    const params = useParams();
    const chatRoomId = params.id;
    const [chatRoomDetail, setChatRoomDetail] = useState<ChatRoomDetail | null>(null);
    const [messages, setMessages] = useState<Message[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchChatRoomData = async () => {
            try {
                // 채팅방 정보 조회
                const roomResponse = await axios.get(`http://localhost:8080/api/v1/chatrooms/${chatRoomId}`);
                setChatRoomDetail(roomResponse.data.data);
        
                // 메시지 목록 조회
                const messagesResponse = await axios.get(`http://localhost:8080/api/v1/chatrooms/${chatRoomId}/messages`);
                setMessages(messagesResponse.data.data.content);
            } catch (error) {
                console.error('데이터 조회 실패:', error);
            } finally {
                setIsLoading(false);
            }
        };
    
        fetchChatRoomData();
      }, [chatRoomId]);
    
    if (isLoading) {
        return <div className="flex justify-center items-center h-screen">로딩 중...</div>;
    }

    return (
        <div className="flex flex-col h-screen">
        {/* 채팅방 헤더 */}
        <div className="bg-white border-b p-4">
            <h1 className="text-xl font-bold">{chatRoomDetail?.group.groupName}</h1>
            <p className="text-sm text-gray-500 flex items-center gap-1">
            <Users className="w-4 h-4" /> {/* 사람 아이콘 추가 */}
            {chatRoomDetail?.group.participantCount}명
            </p>
        </div>

        {/* 메시지 목록 */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
            {messages.map((message: Message) => (
            <div key={message.id} className="flex flex-col">
                <div className="flex items-center gap-2">
                <span className="font-bold">{message.senderNickname}</span>
                <span className="text-xs text-gray-500">
                    {new Date(message.createdAt[0], message.createdAt[1] - 1, message.createdAt[2], 
                            message.createdAt[3], message.createdAt[4], message.createdAt[5]).toLocaleString()}
                </span>
                </div>
                <p className="mt-1 bg-gray-100 rounded-lg p-2 max-w-[70%]">
                {message.content}
                </p>
            </div>
            ))}
        </div>

        {/* 메시지 입력 */}
        <div className="border-t p-4">
            <div className="flex gap-2">
            <input
                type="text"
                className="flex-1 border rounded-lg px-4 py-2"
                placeholder="메시지를 입력하세요"
            />
            <button 
                className="bg-blue-500 text-white px-4 py-2 rounded-lg"
            >
                전송
            </button>
            </div>
        </div>
        </div>
    );
}