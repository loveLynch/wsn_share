package bytedance.arrays;

import java.util.Scanner;

/**
 * @author Lynch
 * @date 2019/8/5 17:42
 */
public class SearchArrayTarget {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int N = input.nextInt();
        int[] nums = new int[N];
        for (int i = 0; i < N; i++) {
            nums[i] = input.nextInt();
        }
        int target = input.nextInt();
        System.out.println(search(nums, target));
    }

    public static int search(int[] nums, int target) {
        if (nums.length <= 0 || nums == null)
            return -1;
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target)
                return mid;
            if (nums[left] <= nums[mid]) {//左边是递增子序列
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }

            } else {//右边是递增子序列
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }

            }
        }
        return -1;
    }
}
