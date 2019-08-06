package bytedance.arrays;

import java.util.Scanner;

/**
 * @author Lynch
 * @date 2019/8/5 17:29
 */
public class MaxIsland {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int M = input.nextInt();
        int N = input.nextInt();
        int[][] grid = new int[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++)
                grid[i][j] = input.nextInt();
        }
        System.out.println(maxAreaOfIsland(grid));
    }

    public static int maxAreaOfIsland(int[][] grid) {
        if (grid.length == 0 || grid == null)
            return 0;
        int max = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {//等于1才进入递归判断
                    int num = getIslanArea(grid, i, j);
                    max = Math.max(num, max);
                }

            }
        }
        return max;

    }

    /**
     * 递归，类似深度优先搜索
     *
     * @param grid
     * @param i
     * @param j
     * @return
     */
    public static int getIslanArea(int[][] grid, int i, int j) {
        if (i >= 0 && j >= 0 && i < grid.length && j < grid[0].length && grid[i][j] == 1) {
            grid[i][j] = 0;//将其置为0.防止下行再判断
            return 1 + getIslanArea(grid, i - 1, j) + getIslanArea(grid, i + 1, j) + getIslanArea(grid, i, j - 1) + getIslanArea(grid, i, j + 1);
        }
        return 0;

    }
}
