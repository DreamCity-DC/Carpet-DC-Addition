# Carpet DC Addition 规则文档

## 快速导航

- [optimizeMapPixelsUpdate — 优化展示框地图更新](#optimizemappixelsupdate--优化展示框地图更新)

## optimizeMapPixelsUpdate — 优化展示框地图更新

移植 Paper 的展示框地图同步优化：不再向当前维度中的所有玩家广播展示框地图同步数据，仅为正在追踪展示框的玩家执行同步操作。

在放置有大量展示框地图的区域（例如大量 [image2map ](https://modrinth.com/mod/image2map)地图画），此优化可以极大降低网络流量压力。

### 原版行为与修复方案

原版 `ServerEntity.sendChanges()` 的展示框地图分支会遍历整个 `level.players()`，因此当地图发送数据更新时，当前维度中的所有玩家（即使不在地图附近）也会收到全量地图像素数据。这带来很大的网络压力。

地图为每个玩家保存像素和标记的 dirty 状态，没有变化时通常不发像素。但大量地图首次同步时，不必要的接收者仍然会极大地放大传输量。

开启本规则后：

1. 展示框地图的接收者改为正在追踪该展示框的玩家。
2. 展示框没有任何追踪者时，跳过其地图查询和同步分支；展示框实体 metadata 同步仍保留原版逻辑。
3. `MapItemSavedData.removeDecoration()` 只有实际移除了标记，才标记 decorations dirty，避免把无变化操作转换为更新包。

注：本规则可以与 Entity View Distance 模组配合，调整展示框实体的追踪距离，来进一步优化地图密集区域的网络性能。
