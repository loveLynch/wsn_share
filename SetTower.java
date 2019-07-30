import java.util.Scanner;

/*
 *@author Lynch
 *@date 2019/7/30 14:18
 */
public class SetTower {
    public static void main(String[] args) {
        //1.数据处理
        Scanner input = new Scanner(System.in);
        int N = Integer.parseInt(input.nextLine());
        int[][] box = new int[N][3];
        int[][] heap = new int[N][2];
        String[] lengthArray = input.nextLine().split(" ");
        String[] weightArray = input.nextLine().split(" ");
        for (int i = 0; i < N; i++) {
            box[i][0] = Integer.parseInt(lengthArray[i]);
            box[i][1] = Integer.parseInt(weightArray[i]);
            box[i][2] = 0;
        }


        //2.算法部分
        int sumMax = 0;
        for (int i = 0; i < box.length; i++) {
            //2.1 初始赋值，循环以每个box为起点
            heap[0][0] = box[i][0];
            heap[0][1] = box[i][1];
            box[i][2] = 1;
            //2.2 递归取值
            sumMax = Math.max(sumMax, findBox(heap, box, 0));
            //2.3 还原数据
            for (int[] s :box)
            s[2] = 0;
            for (int []s :heap)
            {
                s[0] = 0;
                s[1] = 0;
            }
        }
        System.out.println(sumMax+1);
    }

    /**
     * @param heap    堆积木所用的积木数据存到二维数据heap里面，其中heap[i][0]为长宽，heap[i][1]为重量
     * @param box     积木的数据全部存到二维数组box里面，其中box[i][0]为长宽，box[i][1]为重量，box[i][2]为是否被使用的标志
     * @param current
     * @return
     */
    public static int findBox(int[][] heap, int[][] box, int current) {
        //1.递归基（检查合理性)
        int maxHeap = current;
        if (current == box.length)
            return box.length - 1;
        for (int i = 0; i < current; i++) {
            int sum = 0;
            for (int j = i + 1; j <= current; j++)
                sum += heap[j][1];
            if (sum > 7 * heap[i][1])
                return current - 1;
        }
        //2.递归函数
        for (int i = 0; i < box.length; i++) {
            if (box[i][0] < heap[current][0] && box[i][2] == 0) {
                heap[current + 1][0] = box[i][0];
                heap[current + 1][1] = box[i][1];
                box[i][2] = 1;
                maxHeap = Math.max(maxHeap, findBox(heap, box, current + 1));
                //回溯操作
                box[i][2] = 0;
                heap[current + 1][0] = 0;
                heap[current + 1][1] = 0;
            }
        }
        return maxHeap;
    }
}
