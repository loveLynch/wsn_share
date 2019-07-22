from collections import Counter
'''
思路：遍历0-9每一个数字，计算每一个数字出现k次时候的最小花费
细节：
1.计算过程中使用gap表示距离当前数字i的花费
2.因为要输出具体变化后的结果，所以需要考虑如何变化，如果要变化的值小于当前值，则从前往后替代，如果大于当前值，则从后往前替代（为了保证字典序最小）
'''
n, k = map(int, input().split())
s = input()
d = Counter(list(map(int, s)))
res = float("inf")
ans = "A"
for i in range(10):
    tmp_s = s
    need = k - d[i]
    cost = 0
    gap = 1
    while need > 0:
        if i + gap <= 9:
            if d[i + gap] < need:
                tmp_s = tmp_s.replace(str(i + gap), str(i))
                cost += d[i + gap] * gap
                need -= d[i + gap]
            else:
                tmp_s = tmp_s.replace(str(i + gap), str(i), need)
                cost += need * gap
                break
        if i - gap >= 0:
            if d[i - gap] < need:
                tmp_s = tmp_s.replace(str(i - gap), str(i))
                cost += d[i - gap] * gap
                need -= d[i - gap]
            else:
                tmp_s = tmp_s[::-1]
                tmp_s = tmp_s.replace(str(i - gap), str(i), need)
                tmp_s = tmp_s[::-1]
                cost += need * gap
                break
        gap += 1
        print(tmp_s)
    if cost < res:
        ans = tmp_s
        res = cost
    elif cost == res and tmp_s < ans:
        ans = tmp_s

print(res)
print(ans)