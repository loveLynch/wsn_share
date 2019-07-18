#!/usr/bin/python3
# -*- coding: utf-8 -*-
# @Time    : 2019-07-13 14:59
# @Author  : lynch
"""
Deep Q network,
Using:
Tensorflow: 1.0
gym: 0.8.0
"""

from wsn_env import Env
from DQN import DeepQNetwork
import matplotlib.pyplot as plt
import numpy as np

env = Env()

RL = DeepQNetwork(n_actions=env.n_actions, n_features=env.n_features, learning_rate=0, e_greedy=0,
                  replace_target_iter=300, memory_size=3000,
                  e_greedy_increment=0.0002, )

total_steps = 0
reware = []
for i_episode in range(env.times):

    observation = env.reset()

    ep_r = 0
    while True:

        action = RL.choose_action(observation)

        observation_, reward, done = env.step(action)
        RL.store_transition(observation, action, reward, observation_)
        if total_steps > 200 and total_steps % 5 == 0:
            RL.learn()

        ep_r += reward
        reware.append(reward)

     # if done:
        #     print('Epi: ', i_episode,
        #           '| Ep_r: ', round(ep_r, 4),
        #           '| Epsilon: ', round(RL.epsilon, 2))
        #     break

        observation = observation_
        total_steps += 1
        if total_steps % 1000 == 0:
            print('Epi: ', i_episode,
                  '| Ep_r: ', round(ep_r, 4),
                  '| Epsilon: ', round(RL.epsilon, 2))
            break
        # print(total_steps)
        if total_steps == 80001:
            print(RL.cost_his)
            RL.plot_cost()
            plt.xlabel('iter_steps')
            plt.ylabel('reward')
            print(reware)
            plt.plot(range(len(reware)), np.array(reware))
            plt.show()