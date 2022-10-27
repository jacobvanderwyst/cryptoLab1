import os

path="/var/www/html/"
filename="server"
os.system("sudo rm -rf /var/www/html/cryptoLab1")
os.system("cd /var/www/html && git clone https://github.com/jacobvanderwyst/cryptoLab1.git")
os.system("find -name \"*.java\" > sources.txt")
os.system("javac @sources.txt")
os.system("echo \"compiled classes\"")
os.system("echo \"cd .. && python3 cryptoLab1/compile.py && cd cryptoLab1 && clear\"")
os.system("tree")
