import os
import json
import sys

def convert_json_to_kv(input_file, output_file):
    """
    将单个JSON文件转换为key=value格式文本文件
    
    参数:
        input_file (str): 输入JSON文件路径
        output_file (str): 输出文本文件路径
    """
    try:
        with open(input_file, 'r', encoding='utf-8') as infile:
            data = json.load(infile)
        
        with open(output_file, 'w', encoding='utf-8') as outfile:
            for key, value in data.items():
                outfile.write(f"{key}={value}\n")
        
        print(f"转换成功：{input_file} -> {output_file}")
    
    except FileNotFoundError:
        print(f"错误：输入文件 {input_file} 未找到")
    except json.JSONDecodeError:
        print(f"错误：文件 {input_file} 不是有效的JSON格式")
    except Exception as e:
        print(f"转换文件 {input_file} 时发生未知错误: {e}")


def convert_json_folder(input_folder, output_folder):
    """
    遍历输入文件夹中的所有JSON文件，转换为key=value格式并保存到输出文件夹
    
    参数:
        input_folder (str): 输入文件夹路径
        output_folder (str): 输出文件夹路径
    """
    try:
        for root, dirs, files in os.walk(input_folder):
            for file in files:
                if file.lower().endswith('.json'):
                    input_file = os.path.join(root, file)
                    rel_path = os.path.relpath(input_file, input_folder)
                    output_rel_file = os.path.splitext(rel_path)[0] + '.lang'
                    output_file = os.path.join(output_folder, output_rel_file)
                    
                    os.makedirs(os.path.dirname(output_file), exist_ok=True)
                    convert_json_to_kv(input_file, output_file)
    except Exception as e:
        print(f"遍历文件夹时发生错误：{e}")


def main():
    if len(sys.argv) != 3:
        print("用法：python script.py 输入路径 输出路径")
        print("输入路径可以是单个JSON文件或包含JSON文件的文件夹")
        print("输出路径将对应生成文本文件或文件夹结构")
        return

    input_path = sys.argv[1]
    output_path = sys.argv[2]

    if os.path.isfile(input_path):
        if input_path.lower().endswith('.json'):
            print(f"处理单个文件：{input_path}")
            convert_json_to_kv(input_path, output_path)
        else:
            print(f"错误：文件 {input_path} 不是JSON格式文件")
    elif os.path.isdir(input_path):
        print(f"处理文件夹：{input_path}，输出到：{output_path}")
        if os.path.exists(output_path):
            if not os.path.isdir(output_path):
                print(f"错误：输出路径 {output_path} 已存在，但不是一个文件夹")
                return
        convert_json_folder(input_path, output_path)
    else:
        print(f"输入路径无效或不可读：{input_path}")


if __name__ == "__main__":
    main()