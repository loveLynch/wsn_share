package bytedance.arrays;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Lynch
 * @date 2019/8/6 14:06
 */
public class LongestConsecutiveArray {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int N = input.nextInt();
        int[] nums = new int[N];
        for (int i = 0; i < N; i++) {
            nums[i] = input.nextInt();
        }
        System.out.println(longestConsecutive(nums));
    }

    public static int longestConsecutive(int[] nums) {
        if (nums.length == 0 || nums == null)
            return 0;
        int maxLength = 0;
        Set<Integer> store = new HashSet<>();
        //利用set去重，存储各元素
        for (int num : nums)
            store.add(num);
        for (int i = 0; i < nums.length; i++) {
            //判断num[i]-1是否在set中，从而确定最小的起点
            if (!store.contains(nums[i] - 1)) {
                int y = nums[i] + 1;
                //一次累加，判断该元素是否在集合中
                while (store.contains(y))
                    y++;
                maxLength = Math.max(maxLength, y - nums[i]);
            }
        }

        return maxLength;
    }
}
