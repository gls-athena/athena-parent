#!/bin/bash

# 批量更新所有子模块的POM文件版本为CI友好格式
# 此脚本会将所有子模块中的硬编码版本号替换为 ${revision}

echo "开始更新所有模块的版本配置..."

# 定义要替换的版本号
OLD_VERSION="0.0.1-SNAPSHOT"
NEW_VERSION="\${revision}\${changelist}\${sha1}"

# 查找并更新所有POM文件中的版本号
find . -name "pom.xml" -not -path "./target/*" | while read pom_file; do
    echo "处理文件: $pom_file"

    # 使用sed替换parent标签中的version
    sed -i.bak "s|<version>$OLD_VERSION</version>|<version>$NEW_VERSION</version>|g" "$pom_file"

    # 删除备份文件
    rm -f "$pom_file.bak"
done

echo "版本更新完成！"
echo ""
echo "现在可以使用以下命令进行构建："
echo "mvn clean install"
echo ""
echo "或使用动态版本号："
echo "mvn clean install -Drevision=1.0.0 -Dchangelist=-RC -Dsha1=.20250818"
