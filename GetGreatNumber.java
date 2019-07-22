import java.util.ArrayList;
import java.util.Scanner;

public class GetGreatNumber {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String NK = input.nextLine();
        String originalNum = input.nextLine();
        Integer N = Integer.valueOf(NK.split(" ")[0]);
        Integer K = Integer.valueOf(NK.split(" ")[1]);
        if (K <= N) {
            if (originalNum.length() == N) {
                ArrayList<String> greatNum = getSpeedAndNum(originalNum, K);
                System.out.println(greatNum.get(0));
                System.out.println(greatNum.get(1));
            } else {
                System.out.println("the input num length is error!");
            }
        } else {
            System.out.println("the great num length over the sum");
        }
    }

    private static ArrayList<String> getSpeedAndNum(String originalNum, Integer k) {
        ArrayList<String> result = new ArrayList<>();
        int[] countNum = getNumCount(originalNum);
        int res = Integer.MAX_VALUE;
        String ans = "A";
        for (int i = 0; i < 10; i++) {
            String tmpNum = originalNum;
            int need = k - countNum[i];
            int cost = 0;
            int gap = 1;
            while (need > 0) {
                if (i + gap <= 9) {
                    if (countNum[i + gap] < need) {
                        tmpNum = tmpNum.replace(String.valueOf(i + gap), String.valueOf(i));
                        cost += countNum[i + gap] + gap;
                        need -= countNum[i + gap];
                    } else {
                        tmpNum = tmpNum.replace(String.valueOf(i + gap), String.valueOf(need));
                        cost += need * gap;
                        break;
                    }
                }
                if (i - gap >= 0) {
                    if (countNum[i - gap] < need) {
                        tmpNum = tmpNum.replace(String.valueOf(i - gap), String.valueOf(i));
                        cost += countNum[i - gap] + gap;
                        need -= countNum[i - gap];
                    } else {
                        StringBuilder builder1 = new StringBuilder(tmpNum);
                        builder1.reverse();
                        tmpNum = builder1.toString();
                        tmpNum = tmpNum.replace(String.valueOf(i - gap), String.valueOf(need));
                        StringBuilder builder2 = new StringBuilder(tmpNum);
                        builder2.reverse();
                        tmpNum = builder2.toString();
                        cost += need * gap;
                        break;
                    }
                }
                System.out.println(tmpNum);
                gap += 1;

            }
            if (cost < res) {
                ans = tmpNum;
                res = cost;
            } else if (cost == res && Integer.valueOf(tmpNum) < Integer.valueOf(ans)) {
                ans = tmpNum;
            }
        }
        result.add(String.valueOf(res));
        result.add(ans);
        return result;
    }

    public static int[] getNumCount(String originalNum) {
        int[] countNum = new int[10];
        char[] numArray = originalNum.toCharArray();
        for (int i = 0; i < numArray.length; i++) {
            countNum[numArray[i] - '0'] = countNum[numArray[i] - '0'] + 1;

        }
        return countNum;
    }
}
