import { Input, theme } from "antd";
import { SearchOutlined } from "@ant-design/icons";
import React from "react";
import { useRouter } from "next/navigation";

/*Search bar */
export const SearchInput = () => {
  const router = useRouter();
  return (
    <div
      className="search-input"
      key="SearchOutlined"
      aria-hidden
      style={{
        display: "flex",
        alignItems: "center",
        marginInlineEnd: 24,
      }}
    >
      <Input.Search
        style={{
          borderRadius: 4,
          marginInlineEnd: 12,
        }}
        placeholder="Search Questions"
        onSearch={(value) => {
          router.push(`/questions?q=${value}`);
        }}
      />
    </div>
  );
};
