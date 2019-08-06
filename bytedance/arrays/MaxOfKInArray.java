package bytedance.arrays;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * @author Lynch
 * @date 2019/8/6 11:47
 */
public class MaxOfKInArray {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int N = input.nextInt();
        int[] nums = new int[N];
        for (int i = 0; i < N; i++) {
            nums[i] = input.nextInt();
        }
        int k = input.nextInt();
        System.out.println(findKthLargest1(nums, k));
        System.out.println(findKthLargest2(nums, k));
        System.out.println(findKthLargest3(nums, k));
    }

    /**
     * 直接排序查找
     *
     * @param nums
     * @param k
     * @return
     */
    public static int findKthLargest1(int[] nums, int k) {
        int kth = 0;
        Arrays.sort(nums);
        for (int i = nums.length - 1; i >= 0; i--) {
            if (nums.length - i == k)
                kth = nums[i];
        }
        return kth;
    }

    /**
     * 优先队列
     *
     * @param nums
     * @param k
     * @return
     */
    public static int findKthLargest2(int[] nums, int k) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(); // 小顶堆
        for (int val : nums) {
            pq.add(val);
            if (pq.size() > k) // 维护堆的大小为 K
                pq.poll();
        }
        return pq.peek();
    }

    /**
     * 快速排序
     *
     * @param nums
     * @param k
     * @return
     */
    public static int findKthLargest3(int[] nums, int k) {
        int begin = 0;
        int end = nums.length - 1;
        k = nums.length + 1 - k;
        while (begin < end) {
            int pos = partition(nums, begin, end);
            if (pos == k - 1) break;
            else if (pos < k - 1) begin = pos + 1;
            else end = pos - 1;
        }
        return nums[k - 1];
    }

    public static int partition(int[] nums, int l, int r) {
        int less = l - 1;//小于区的下标
        int more = r;//大于区的下标，默认以最后一个下标的数作为划分值
        while (l < more) {
            if (nums[l] < nums[r])
                swap(nums, ++less, l++);
            else if (nums[l] > nums[r])
                swap(nums, --more, l);
            else l++;
        }
        swap(nums, more, r);
        return less + 1;//小于区位置+1可以得到划分的这个数的下标
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}