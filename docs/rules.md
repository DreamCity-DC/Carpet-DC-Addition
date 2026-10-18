# Carpet DC Addition 规则文档

## 快速导航

- [optimizeMapPixelsUpdate — 优化展示框地图更新](#optimizemappixelsupdate--优化展示框地图更新)
- [vaultBlacklistDisabledByNamePrefix — 按玩家名前缀禁用宝库黑名单](#vaultblacklistdisabledbynameprefix--按玩家名前缀禁用宝库黑名单)
- [lightningNoFire — 无火闪电](#lightningnofire--无火闪电)

## optimizeMapPixelsUpdate — 优化展示框地图更新

移植 Paper 的展示框地图同步优化：不再向当前维度中的所有玩家广播展示框地图同步数据，仅为正在追踪展示框的玩家执行同步操作。

在放置有大量展示框地图的区域（例如大量 [image2map ](https://modrinth.com/mod/image2map)地图画），此优化可以极大降低网络流量压力。

- 类型：`boolean`
- 默认值：`false`（关闭，保持原版行为）
- 分类：`dc`、`optimization`、`bugfix`

### 修复思路

原版 `ServerEntity.sendChanges()` 的展示框地图分支会遍历整个 `level.players()`，因此当地图发送数据更新时，当前维度中的所有玩家只要死亡过，或者重进过服务器，其同步状态被标记为丢失，于是就会收到全量地图像素数据，即使他们不在地图附近也会收到。

不必要的接收者导致传输量被放大数倍，玩家越多、地图画越多，每次全体同步带来的卡顿和带宽压力越严重。

开启本规则后：

1. 展示框地图的接收者改为正在追踪该展示框的玩家。
2. 展示框没有任何追踪者时，跳过其地图查询和同步分支；展示框实体 metadata 同步仍保留原版逻辑。
3. `MapItemSavedData.removeDecoration()` 只有实际移除了标记，才标记 decorations dirty，避免把无变化操作转换为更新包。

注：本规则可以与 Entity View Distance 模组配合，调整展示框实体的追踪距离，来进一步优化地图密集区域的网络性能。

## vaultBlacklistDisabledByNamePrefix — 按玩家名前缀禁用宝库黑名单

基于 [Carpet TIS Addition](https://github.com/TISUnion/Carpet-TIS-Addition) 的 [vaultBlacklistDisabled 规则](https://carpet.tis.world/docs/rules#vaultBlacklistDisabled)移植，并增加玩家名前缀限制。

- 类型：`String`
- 默认值：`false`（关闭，保持原版行为）
- 建议值：`false`、`bot_`；也可指定其他非空前缀
- 分类：`dc`、`feature`

设置为 `bot_` 时，名字以 `bot_` 开头的玩家可以重复开启同一个宝库，开箱后不会被加入该宝库的已领奖玩家黑名单。其他玩家仍受原版黑名单限制。

如果同时安装了 Carpet TIS Addition，应保持其全局规则 `vaultBlacklistDisabled` 为 `false`。

## lightningNoFire — 无火闪电

移植自 [Carpet Igny Addition](https://github.com/liuyuexiaoyu1/Carpet-Igny-Addition) 的 [lightningBoltNoFire 规则](https://github.com/liuyuexiaoyu1/Carpet-Igny-Addition/blob/master/docs/rules.md#无火闪电束-lightningboltnofire)

阻止闪电生成火方块，以防止烧毁建筑。闪电其它的所有特性比如伤害、点燃实体、转化实体、避雷针等等均不受影响。

- 类型：`boolean`
- 默认值：`false`（关闭，保持原版行为）
- 分类：`dc`、`feature`
