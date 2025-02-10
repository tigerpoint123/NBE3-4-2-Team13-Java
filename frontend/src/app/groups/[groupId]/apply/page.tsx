import ClientPage from "./ClientPage";

interface Props {
    params: {
        groupId: string;
    };
}

export default function ApplyPage({ params }: Props) {
    return <ClientPage groupId={params.groupId} />;
}
