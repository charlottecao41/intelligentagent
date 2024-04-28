import matplotlib.pyplot as plt
import random

def extend_walls(walls,r):
    ''' for simplicity's sake, the i did not enter coord such as (-1,x) as walls below, so this is just a util to expand the outer walls'''
    walls.extend([(-1,j) for j in range(len(r[0]))])
    walls.extend([(i,-1) for i in range(len(r))])
    walls.extend([(len(R),j) for j in range(len(r[0]))])
    walls.extend([(i,len(R[0])) for i in range(len(r))])
    return walls

def update_util_dict(coord,util,util_dict):
    ''' util dict is to track the util change over iterations'''
    i,j = coord
    if (i,j) not in util_dict:
        util_dict.update({(i,j):[]})
    else:
        util_dict[(i,j)].append(util)
    
    return util_dict

def print_U(u):
    '''Print utility grid for easy viewing'''
    for i in u:
        print(i)
def print_policy(policy):
    for i in range(len(policy)):
        for j in range(len(policy[i])):
            print(f"{(i,j)}:{policy[i][j]}")

def print_utility(u):
    '''print utility like reference utility in question paper, but it is in (row,col) format'''
    for i in range(len(u)):
        for j in range(len(u[i])):
            print(f"{(i,j)}:{u[i][j]}")
def get_util(curr_coord,prev_coord,u, walls):
        ''' If curr_coord is in walls, return previous coord'''
        if curr_coord in walls:
            return u[prev_coord[0]][prev_coord[1]]
        else:
            return u[curr_coord[0]][curr_coord[1]]



def move(command,curr_coord):
    ''' return possible next states given a coordinate and an action'''
    i,j=curr_coord
    if command=='l':
        return (i,j-1),(i+1,j),(i-1,j)
    if command=='r':
        return (i,j+1),(i+1,j),(i-1,j)
    if command=='u':
        return (i-1,j),(i,j+1),(i,j-1)
    if command=='d':
        return (i+1,j),(i,j+1),(i,j-1)
    
def find_best_policy(curr_coord,u,walls):
    '''Given a util grid u, and current coordinate, find the best next action to take'''
    candidate = []
    i,j = curr_coord
    def find_key(e):
        return e[1]
    goal,right_angle1,right_angle2 = move('u',(i,j))
    next_move_util = 0.8*get_util(goal,(i,j),u,walls)+0.1*get_util(right_angle1,(i,j),u,walls)+0.1*get_util(right_angle2,(i,j),u,walls)
    candidate.append(('u',next_move_util))
    goal,right_angle1,right_angle2 = move('d',(i,j))
    next_move_util = 0.8*get_util(goal,(i,j),u,walls)+0.1*get_util(right_angle1,(i,j),u,walls)+0.1*get_util(right_angle2,(i,j),u,walls)
    candidate.append(('d',next_move_util))
    goal,right_angle1,right_angle2 = move('l',(i,j))
    next_move_util = 0.8*get_util(goal,(i,j),u,walls)+0.1*get_util(right_angle1,(i,j),u,walls)+0.1*get_util(right_angle2,(i,j),u,walls)
    candidate.append(('l',next_move_util))
    goal,right_angle1,right_angle2 = move('r',(i,j))
    next_move_util = 0.8*get_util(goal,(i,j),u,walls)+0.1*get_util(right_angle1,(i,j),u,walls)+0.1*get_util(right_angle2,(i,j),u,walls)
    candidate.append(('r',next_move_util))

    candidate.sort(reverse=True,key=find_key)
    return candidate[0]

#Value iter
def value_iter(r,terminal,walls,error,gamma):
    '''takes in reward grid r, terminal state (if applicable), walls as an array, discount factor gamma, and error as threshold error 
    
    if enable_error == True, will use error as policy evaluation metric, otherwise will use the number of iterations'''
    count = 0
    util_dict = {}
    u = [[0 for i in range(len(r[0]))] for i in range (len(r))]
    above_threshold = True
    while above_threshold:
        above_threshold = False
        count = count + 1
        e = 0
        for i in range(len(r)):
            for j in range(len(r[0])):
                if (i,j) in walls:
                    u[i][j]=0
                elif (i,j) in terminal:
                    u[i][j]=r[i][j]
                else:
                    #Up, Left, Right, Down
                    next_move_util = [0.8*get_util((i-1,j),(i,j),u,walls)+0.1*get_util((i,j-1),(i,j),u,walls)+0.1*get_util((i,j+1),(i,j),u,walls),
                                0.8*get_util((i,j-1),(i,j),u,walls)+0.1*get_util((i+1,j),(i,j),u,walls)+0.1*get_util((i-1,j),(i,j),u,walls),
                                0.8*get_util((i,j+1),(i,j),u,walls)+0.1*get_util((i+1,j),(i,j),u,walls)+0.1*get_util((i-1,j),(i,j),u,walls),
                                0.8*get_util((i+1,j),(i,j),u,walls)+0.1*get_util((i,j-1),(i,j),u,walls)+0.1*get_util((i,j+1),(i,j),u,walls),
                                ]
                    
                    #find max of next move's utility
                    max_val = max(next_move_util)

                    #Util of current state is the reward of current state + discount*max_util of best action
                    e = abs(u[i][j]-max_val*gamma-r[i][j])*(1-gamma)/gamma
                    if error< e:
                        above_threshold = True
                    u[i][j]=max_val*gamma+r[i][j]
                    util_dict = update_util_dict((i,j),u[i][j],util_dict=util_dict)

        #Given a util grid, find the policy of that util grid
        policy = []
        for i in range(len(u)):
            policy.append([])
            for j in range(len(u[0])):
                if (i,j) not in walls:
                    action,value = find_best_policy((i,j),u,walls)
                    policy[i].append(action)
                else:
                    policy[i].append('na')
    return count,u,util_dict,policy
      
def policy_iter(r,terminal,walls,gamma,enable_error,threshold):
    '''takes in reward grid r, terminal state (if applicable), walls as an array, discount factor gamma, and error as threshold error '''
    util_dict = {}
    count = 0
    u = [[0 for i in range(len(r[0]))] for i in range (len(r))]
    #init policy to default moving left
    policy=[]
    for i in range(len(r)):
        policy.append([])
        for j in range(len(r[0])):
            policy[i].append('l')

    changed=True
    while changed:
        changed=False
        above_threshold = True
        
        k=0
        #POLICY EVAL
        count = count + 1
        while above_threshold:
            k=k+1
            above_threshold = False
            for i in range(len(policy)):
                for j in range(len(policy[i])):
                    #Up, Left, Right, Down
                    if (i,j) in terminal:
                        u[i][j]=r[i][j]
                    elif (i,j) in walls:
                        u[i][j]=0
                    else:
                        goal,right_angle1,right_angle2 = move(policy[i][j],(i,j))
                        next_move_util = 0.8*get_util(goal,(i,j),u,walls)+0.1*get_util(right_angle1,(i,j),u,walls)+0.1*get_util(right_angle2,(i,j),u,walls)
                        if enable_error and abs(u[i][j]-next_move_util*gamma - r[i][j])>threshold:
                            above_threshold = True

                        if not(enable_error) and k< threshold:
                            above_threshold = True
                        u[i][j]=next_move_util*gamma + r[i][j]
                        util_dict = update_util_dict((i,j),u[i][j],util_dict=util_dict)

                    
        #UPDATE POLICY
        for i in range(len(policy)):
            for j in range(len(policy[i])):
                if (i,j) in terminal:
                    policy[i][j]='na'
                elif (i,j) in walls:
                    policy[i][j]='na'
                else:
                    new_policy,new_value = find_best_policy((i,j),u,walls)
                    goal,right_angle1,right_angle2 = move(policy[i][j],(i,j))
                    next_move_util = 0.8*get_util(goal,(i,j),u,walls)+0.1*get_util(right_angle1,(i,j),u,walls)+0.1*get_util(right_angle2,(i,j),u,walls)
                    if new_value>next_move_util:
                        changed=True
                        policy[i][j]=new_policy

    return count,u,util_dict,policy

def generate_random_board(size,wall_probability,reward_state_prob):
    ''' generate a random board with sizexsize, wall_probabily*size*size walls and reward_state_prob*size*size +1/-1 states'''
    r = []
    walls = []
    for i in range(size):
        r.append([])
        for j in range(size):
            if random.uniform(0, 1) < wall_probability:
                r[i].append(0)
                walls.append((i,j))
            elif random.uniform(0, 1) < reward_state_prob:
                if random.uniform(0, 1) < 0.5:
                    r[i].append(1)
                else:
                    r[i].append(-1)
            else:
                r[i].append(-0.04)
    
    return r,walls


if __name__ == '__main__':

    R=[
    [1,0,1,-0.04,-0.04,1],
    [-0.04,-1,-0.04,1,0,-1],
    [-0.04,-0.04,-1,-0.04,1,-0.04],
    [-0.04,-0.04,-0.04,-1,-0.04,1],
    [-0.04,0,0,0,-1,-0.04],
    [-0.04,-0.04,-0.04,-0.04,-0.04,-0.04]
    ]

    START = (3,2)

    #init walls
    WALLS = [(0,1),(1,4),(4,1),(4,2),(4,3)]
    WALLS = extend_walls(WALLS,R)
    GAMMA = 0.99
    TERMINAL = []
    ERROR = 0.001
    ITER_THRESH=10
    print("Value iteration of the problem: ")
    count,u,util_dict,policy = value_iter(R,TERMINAL,WALLS,ERROR,GAMMA)
    print(f"For error threshold {ERROR}, Number of iterations: {count}")
    print("--------POLICY---------")
    print_utility(policy)
    print("--------UTILITY---------")
    print_utility(u)
    plt.figure(figsize=(10,10))
    for state in util_dict:
        plt.plot([i for i in range(len(util_dict[state]))], util_dict[state], label=state)

    plt.legend(fontsize="10")
    plt.savefig('value_iter.png')

    print("Policy iteration of the problem: ")
    count,u,util_dict,policy = policy_iter(R,TERMINAL,WALLS,GAMMA,True,ERROR)
    print(f"For error threshold {ERROR}, Number of iterations: {count}")
    print("--------POLICY---------")
    print_utility(policy)
    print("--------UTILITY---------")
    print_utility(u)
    plt.figure(figsize=(10,10))
    for state in util_dict:
        plt.plot([i for i in range(len(util_dict[state]))], util_dict[state], label=state)

    plt.legend(fontsize="10")
    plt.savefig('policy_iter.png')

    print("Modified Policy iteration of the problem: ")
    count,u,util_dict,policy = policy_iter(R,TERMINAL,WALLS,GAMMA,False, ITER_THRESH)
    print(f"For iteration threshold {ITER_THRESH}, Number of iterations: {count}")
    print("--------POLICY---------")
    print_utility(policy)
    print("--------UTILITY---------")
    print_utility(u)
    plt.figure(figsize=(10,10))
    for state in util_dict:
        plt.plot([i for i in range(len(util_dict[state]))], util_dict[state], label=state)

    plt.legend(fontsize="10")
    plt.savefig(f'policy_iter_{ITER_THRESH}_iter.png')


    
    #PART 2
    val_count = []
    pol_count=[]
    MAX_SIZE=30

    #generate 5-30 size boards
    for size in range(5,MAX_SIZE):
        count1=0
        count2=0
        for n in range(5):
            R,WALLS = generate_random_board(size,wall_probability=0.2,reward_state_prob=0.2)

            START = (3,2)

            #Init utility
            U = [[0 for i in range(len(R[0]))] for i in range (len(R))]
            #init walls
            WALLS = extend_walls(WALLS,R)
            GAMMA = 0.99
            TERMINAL = []
            ERROR = 0.001
            count,u,util_dict,policy = value_iter(R,TERMINAL,WALLS,ERROR,GAMMA)
            count1=count1+count

            count,u,util_dict,policy = policy_iter(R,TERMINAL,WALLS,GAMMA,False, ITER_THRESH)
            count2=count+count2

        val_count.append(count1/5)
        pol_count.append(count2/5)

    print("printing size figures...")
    plt.figure()
    plt.plot([i for i in range(5,MAX_SIZE)], val_count)
    plt.savefig('changing_size_value.png')

    plt.figure()
    plt.plot([i for i in range(5,MAX_SIZE)], pol_count)
    plt.savefig('changing_size_policy.png')

    #generate 0.1-0.9 probability wall_probability
    val_count = []
    pol_count=[]
    for wall_probability in [0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9]:
        
        count1=0
        count2=0
        for n in range(5):
            R,WALLS = generate_random_board(wall_probability=wall_probability,size=30,reward_state_prob=0.2)

            START = (3,2)

            #init walls
            WALLS = extend_walls(WALLS,R)
            GAMMA = 0.99
            TERMINAL = []
            ERROR = 0.001
            count,u,util_dict,policy = value_iter(R,TERMINAL,WALLS,ERROR,GAMMA)
            count1=count1+count

            count,u,util_dict,policy = policy_iter(R,TERMINAL,WALLS,GAMMA,False, ITER_THRESH)
            count2=count+count2
        
        val_count.append(count1/5)
        pol_count.append(count2/5)

    print("printing probability figures...")
    plt.figure()
    plt.plot([0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9], val_count)
    plt.savefig('changing_wall_value.png')
    plt.figure()
    plt.plot([0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9], pol_count)
    plt.savefig('changing_wall_policy.png')

    #generate 0.1-0.9 probability reward_prob
    val_count = []
    pol_count=[]
    for reward_state_prob in [0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9]:
        
        count1=0
        count2=0
        for n in range(5):
            R,WALLS = generate_random_board(reward_state_prob=reward_state_prob, size=30,wall_probability=0.2)

            START = (3,2)
            #init walls
            WALLS = extend_walls(WALLS,R)
            GAMMA = 0.99
            TERMINAL = []
            ERROR = 0.001
            count,u,util_dict,policy = value_iter(R,TERMINAL,WALLS,ERROR,GAMMA)
            count1=count1+count

            count,u,util_dict,policy = policy_iter(R,TERMINAL,WALLS,GAMMA,False, ITER_THRESH)
            count2=count+count2
        
        val_count.append(count1/5)
        pol_count.append(count2/5)

    print("printing probability figures...")
    plt.figure()
    plt.plot([0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9], val_count)
    plt.savefig('changing_reward_value.png')
    plt.figure()
    plt.plot([0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9], pol_count)
    plt.savefig('changing_reward_policy.png')


