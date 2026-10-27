<h1 align="center">ExpExtraction 经验存储</h1>
<p align="center">
  <a href="https://mc.lunadeer.cn/amusing/ExpExtraction/" target="_blank">
    <img src="https://img.shields.io/badge/文档-点击查看-blue?style=for-the-badge" alt="文档地址">
  </a>
</p>

## 介绍

使用本插件可以将自己的经验存储到附魔之瓶，以备后用或分享给其他人。

## 使用方法

### 经验提取（方法一）

```
/extract [每瓶经验值] [瓶数]
```

> 不指定经验值时，默认提取现有经验（最多一瓶的容量）；不指定瓶数时，默认生成 1 瓶。

例如，`/extract 2000 4` 会尝试生成 4 瓶、每瓶 2000 点经验值。
如果玩家只有 7000 点经验，则只生成 3 瓶，每瓶仍是 2000 点，剩余 1000 点经验不会被提取。

单瓶默认最多储存 2000 点经验；单次指令默认最多生成 99 瓶。
这些上限可以通过配置文件中的 `command-extraction.maximum` 和 `command-extraction.maximum-bottles` 调整。

使用指令后，经验瓶会以最多 99 瓶一堆的方式掉落在玩家附近。

### 经验提取（方法二）

潜行状态下手持空玻璃瓶，对着附魔台右键即可提取经验。

### 使用经验

手持附魔之瓶右键使用即可。

## bStats Statics

<div align="center">
<img src="https://bstats.org/signatures/bukkit/ExpExtraction.svg" alt="" width="100%">
</div>



