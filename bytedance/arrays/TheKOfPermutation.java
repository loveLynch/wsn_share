package bytedance.arrays;

import java.util.*;

/**
 * @author Lynch
 * @date 2019/8/6 14:51
 */
public class TheKOfPermutation {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int n = input.nextInt();
        int k = input.nextInt();
        System.out.println(getPermutation(n, k));

    }

    public static String getPermutation(int n, int k) {
        StringBuilder result = new StringBuilder();
        if (n == 1) {
            return "1";
        } else if (n == 2) {
            if (k == 1) {
                return "12";
            } else {
                return "21";
            }
        } else {
            int firstNum = k / getCount(n - 1);
            getPermutationCore(n, k, firstNum, result);
        }
        int nextNum = result.toString().charAt(result.toString().length() - 1) - '0';
        result.deleteCharAt(result.toString().length() - 1);
        Set<Integer> all = new HashSet<>();
        for (int i = 1; i <= n; i++)
            all.add(i);
        char[] resultChar = result.toString().toCharArray();
        for (int i = 0; i < resultChar.length; i++) {
            all.remove(resultChar[i] - '0');
        }
        List<Integer> twoList = new ArrayList<>();
        for (int two : all)
            twoList.add(two);
        Collections.sort(twoList);
        if (nextNum == 1) {
            result.append(twoList.get(0));
            result.append(twoList.get(1));
        } else {
            result.append(twoList.get(1));
            result.append(twoList.get(0));
        }


        return result.toString();

    }

    public static void getPermutationCore(int n, int k, int firstNum, StringBuilder result) {
        if (n > 2) {
            int count = getCount(n - 1);
            int nowFirst = k / count;
            int nowNext = k % count;
            if (nowFirst >= firstNum)
                nowFirst++;
            result.append(nowFirst);
            if (n == 3)
                result.append(nowNext);
            getPermutationCore(n - 1, nowNext, nowFirst, result);
        }


    }

    public static int getCount(int n) {
        int count = 1;
        while (n > 1) {
            count = count * n;
            n--;
        }
        return count;
    }
}
