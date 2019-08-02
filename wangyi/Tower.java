package wangyi;

import com.sun.org.apache.xerces.internal.xs.StringList;

import java.util.*;

/**
 * @author Lynch
 * @date 2019/8/2 17:13
 * 塔
 * 时间限制：1秒
 * <p>
 * 空间限制：262144K
 * <p>
 * 小易有一些立方体，每个立方体的边长为1，他用这些立方体搭了一些塔。
 * 现在小易定义：这些塔的不稳定值为它们之中最高的塔与最低的塔的高度差。
 * 小易想让这些塔尽量稳定，所以他进行了如下操作：每次从某座塔上取下一块立方体，并把它放到另一座塔上。
 * 注意，小易不会把立方体放到它原本的那座塔上，因为他认为这样毫无意义。
 * 现在小易想要知道，他进行了不超过k次操作之后，不稳定值最小是多少。
 * <p>
 * 输入描述:
 * 第一行两个数n,k (1 <= n <= 100, 0 <= k <= 1000)表示塔的数量以及最多操作的次数。
 * 第二行n个数，ai(1 <= ai <= 104)表示第i座塔的初始高度。
 * <p>
 * 输出描述:
 * 第一行两个数s, m，表示最小的不稳定值和操作次数(m <= k)
 * 接下来m行，每行两个数x,y表示从第x座塔上取下一块立方体放到第y座塔上。
 * <p>
 * 输入例子1:
 * 3 2
 * 5 8 5
 * <p>
 * 输出例子1:
 * 0 2
 * 2 1
 * 2 3
 */
public class Tower {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String NK = input.nextLine();
        int N = Integer.parseInt(NK.split(" ")[0]);
        int K = Integer.parseInt(NK.split(" ")[1]);
        int[] towers = new int[N];
        for (int i = 0; i < N; i++) {
            towers[i] = input.nextInt();
        }
        List<String> resultList = getMoveSolution(towers, K);
        for (String result : resultList)
            System.out.println(result);
    }

    private static List<String> getMoveSolution(int[] towers, int k) {
        int max = towers[0];
        int min = towers[0];
        HashMap<Integer, Integer> numCount = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> numLocation = new HashMap<>();
        for (int i = 0; i < towers.length; i++) {
            if (towers[i] > max)
                max = towers[i];
            if (towers[i] < min)
                min = towers[i];
            if (numCount.containsKey(towers[i])) {
                numCount.put(towers[i], numCount.get(towers[i]++));
                ArrayList<Integer> list = numLocation.get(towers[i]);
                list.add(i);
                numLocation.put(towers[i], list);

            } else {
                numCount.put(towers[i], 1);
                ArrayList<Integer> list = new ArrayList<>();
                list.add(i);
                numLocation.put(towers[i], list);

            }
        }
        return null;

    }
}
