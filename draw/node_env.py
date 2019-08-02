#!/usr/bin/python3
# -*- coding: utf-8 -*-
# @Time    : 2019-08-02 09:04
# @Author  : lynch
import math
import matplotlib.pyplot as plt
import numpy as np
import random
import xlrd

from math import pi, sin, cos, radians


def GeneratePointInTriangle(point_num, pointA, pointB, pointC):
    """
    三角形内部随机点分布，返回随机点位置坐标集合
    :param point_num:
    :param pointA:
    :param pointB:
    :param pointC:
    :return:
    """
    x = []
    y = []
    for i in range(1, point_num + 1):
        u = random.uniform(0.0, 1.0)
        v = random.uniform(0.0, 1.0 - u)
        pointP = u * pointC + v * pointB + (1.0 - u - v) * pointA;
        x.append(round(pointP[0], 2))
        y.append(round(pointP[1], 2))
    return x, y


class SensorNode(object):
    def __init__(self, id, energy, check_radius, x, y):
        """初始化节点属性"""
        self.id = id
        self.energy = energy
        self.check_radius = check_radius
        self.x = x
        self.y = y

    def get_node_info(self):
        """
        获取节点信息
        :return:
        """
        nodeInfo = []
        nodeInfo.append(self.id)
        nodeInfo.append(self.energy)
        nodeInfo.append(self.x)
        nodeInfo.append(self.y)

        return nodeInfo

    def get_node_location(self):
        """
        获取节点位置
        :return:
        """
        nodeLocation = []
        nodeLocation.append(self.x)
        nodeLocation.append(self.y)

        return nodeLocation

    def get_node_energy(self):
        """
        获取节点能量
        :return:
        """
        return self.energy


class Env(object):
    def __init__(self):
        """
        初始化环境属性
        """
        self.node_num = 36  # 内部节点撒点数
        self.node_num = 36  # 内部节点撒点数
        self.check_radius = 30  # 节点探测半径
        self.initial_energy = 400  # 节点初始能量
        self.env_edge = 250  # 区域边长度

        # self.node_dict, self.edge_node_dict = self.set_node_location()  # 初始化内部移动节点和边缘静态节点信息
        # 目标轨迹设计相关参数,模拟运动
        self.angle = 30  # 与x轴的夹角，初始偏向角
        self.vel = 30  # 初速度
        self.h0 = 200  # 目标节点默认入侵为最左侧，只需设计节点的纵坐标
        self.interval_time = 0.1  # 设置目标坐标保存间隔时间

        # self.target_dict = self.set_target_trace()  # 初始化本次仿真的目标移动轨迹，模拟抛物线运动，设置步长，周期性保持目标节点位置坐标

    def set_point(self, nodeId, pointA, pointB, pointC, pointD):
        node_dict = {}  # 内部节点信息{sensorId:SensorNode}

        x1_1_set, y1_1_set = GeneratePointInTriangle(int(self.node_num / 2), pointA, pointB, pointC)
        x1_2_set, y1_2_set = GeneratePointInTriangle(int(self.node_num / 2), pointD, pointB, pointC)
        # 左下角部分随机点
        for x1, y1 in zip(x1_1_set, y1_1_set):
            node_dict.update({nodeId: SensorNode(nodeId, self.initial_energy, self.check_radius, x1, y1)})
            nodeId = nodeId + 1
        # 右下角部分随机点
        nodeId = self.node_num / 2 + 1
        for x2, y2 in zip(x1_2_set, y1_2_set):
            node_dict.update({nodeId: SensorNode(nodeId, self.initial_energy, self.check_radius, x2, y2)})
            nodeId = nodeId + 1

        # 在区域内画出内部节点
        plt.scatter(x1_1_set, y1_1_set, c='b', marker='o')
        plt.scatter(x1_2_set, y1_2_set, c='b', marker='o')
        return node_dict

    def set_in_node_point(self, i, j):
        """
        :param i: 子区域x轴起点
        :param j: 子区域y轴起点
        :return:
        """
        x_mat = []
        y_mat = []
        i_edge = i + 110
        j_edge = j + 110
        j_pre = j
        while i <= i_edge:
            while j <= j_edge:
                x_mat.append(i)
                y_mat.append(j)
                j += 20
            i += 20
            j = j_pre
        return x_mat, y_mat

    def set_node_location(self):
        """
          读取节点初始信息
          :return:
          """
        node_dict = {}  # 内部节点信息{sensorId:SensorNode}

        edge_node_dict = {}  # 边缘节点信息{sensorId:SensorNode}

        # # 内部节点方案一：内部节点随机均匀分布
        # # 设置矩形区域内两个三角形的四个顶点
        # 方正1
        # self.set_point(1, np.array([0, 0]), np.array([0, 120]), np.array([120, 120]), np.array([120, 0]))
        # # 方正2
        # self.set_point(37, np.array([130, 0]), np.array([130, 120]), np.array([250, 120]), np.array([250, 0]))
        # # 方正3
        # self.set_point(73, np.array([130, 130]), np.array([130, 250]), np.array([250, 250]), np.array([250, 130]))
        # # 方正4
        # self.set_point(109, np.array([0, 130]), np.array([0, 250]), np.array([130, 250]), np.array([120, 130]))
        # 内部节点方案二：均匀分布节点位置,四个小区域逆时针
        # 方正1
        x_mat1, y_mat1 = self.set_in_node_point(0, 0)
        plt.scatter(x_mat1, y_mat1, c='b', marker='o')
        # 方正2
        x_mat2, y_mat2 = self.set_in_node_point(130, 0)
        plt.scatter(x_mat2, y_mat2, c='b', marker='o')
        # 方正3
        x_mat3, y_mat3 = self.set_in_node_point(130, 130)
        plt.scatter(x_mat3, y_mat3, c='b', marker='o')
        # 方正4
        x_mat4, y_mat4 = self.set_in_node_point(0, 130)
        plt.scatter(x_mat4, y_mat4, c='b', marker='o')

        # 边缘节点，整个系统中边缘节点位置不变，只更新能量，节点属性从xlsx文件中读取
        # 打开文件
        data = xlrd.open_workbook(r'nodeInfo.xlsx')
        # 获取表格数目
        nums = len(data.sheets())
        for i in range(nums):
            # 根据sheet顺序打开sheet
            work = data.sheets()[i]
            # 根据sheet名称获取
            sheet1 = data.sheet_by_name('Sheet1')  # 边缘节点
            nrows1 = sheet1.nrows  # 行
            ncols1 = sheet1.ncols  # 列

        # 存储边缘节点位置
        edge_x_mat = []
        edge_y_mat = []
        for i in range(nrows1):
            # print(sheet2.row_values(i))
            edge_node_dict.update({sheet1.row_values(i)[0]: SensorNode(sheet1.row_values(i)[0], self.initial_energy,
                                                                       self.check_radius, sheet1.row_values(i)[1],
                                                                       sheet1.row_values(i)[2])})
            edge_x_mat.append(sheet1.row_values(i)[1])
            edge_y_mat.append(sheet1.row_values(i)[2])
        plt.scatter(edge_x_mat, edge_y_mat, c='r', marker='o')
        # for x, y in zip(x_mat, y_mat):
        #     plt.text(x, y, (x, y), ha="center", va="bottom", fontsize=10)
        return node_dict, edge_node_dict

    def set_target_trace(self):
        """
        设计生成目标节点运动轨迹
        :return:
        """
        target_dict = {}  # 存储目标轨迹坐标
        # 以输入的方式输入目标的初始参数
        # angle = eval(input('Enter the launch angle(in degrees):'))
        #
        # vel = eval(input('Enter the initial velocity(in meters/sec):'))
        # h0 = eval(input('Enter the initial height(in meters):'))
        # time = eval(input('Enter the time interval:'))
        # 设置目标的起始位置
        xpos = 0
        ypos = self.h0
        theta = radians(self.angle)  # 将输入的角度值转换为弧度值
        xvel = self.vel * cos(theta)  # 目标节点的初始速度在x轴上的分量
        yvel = self.vel * sin(theta)  # 目标节点的初始速度在y轴上的分量
        time = self.interval_time

        print('the position:({0:.3f},{1:0.3f})'.format(xpos, ypos))
        xScale = 25  # x坐标放大倍数
        yScale = 30  # y坐标放大倍数
        # 画笔移到铅球的起始位置，准备绘制铅球的运行轨迹
        # t.goto(xpos * xScale, ypos * yScale)

        target_x_set = []
        target_y_set = []
        # 通过while循环绘制铅球的运行轨迹，每隔time秒，取一个点，将所有取到的点连起来
        target_x_set.append(round(xpos, 3))
        target_y_set.append(round(ypos, 3))
        targetId = 1
        target_dict.update({targetId: [round(xpos, 3), round(ypos, 3)]})
        while (0 <= ypos <= self.env_edge) and (0 <= xpos <= self.env_edge):
            targetId = targetId + 1
            xpos = xpos + time * xvel
            ypos = ypos + time * yvel
            # 方案一：产生一个新的偏向角度(-90,90)之间，按步长更新
            # theta = radians(random.uniform(-90, 90))
            # xvel = self.vel * cos(theta)
            # yvel = self.vel * sin(theta)
            # 方案二：模拟抛物运动
            yvel1 = yvel - time * 5
            ypos = ypos + time * (yvel + yvel1) / 2.0
            yvel = yvel1
            print('the position:({0:.3f},{1:0.3f})'.format(xpos, ypos))
            target_x_set.append(round(xpos, 3))
            target_y_set.append(round(ypos, 3))
            target_dict.update({targetId: [round(xpos, 3), round(ypos, 3)]})
            # plt.scatter(target_x_set, target_y_set)
            # t.goto(xpos * xScale, ypos * yScale)
            plt.plot(target_x_set, target_y_set, c='r')

        return target_dict
        # return target_x_set, target_y_set

if __name__ == '__main__':
    env = Env()
    env.set_node_location()
    env.set_target_trace()

    #     绘制初始部署图
    plt.xlabel('node deployment and target trajectory')
    # plt.xlim(-1, env.env_edge + 1)
    # plt.ylim(-1, env.env_edge + 1)
    plt.axis([-1, env.env_edge + 1, -1, env.env_edge + 1])
    ax = plt.gca()
    ax.set_aspect(1)

    plt.show()
