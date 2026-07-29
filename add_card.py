"""添加工作信息卡片。Hermes 调用: python add_card.py "项目A群" "张三说下周一交付" high"""
import json, sys, os
from datetime import datetime

DATA_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'info-cards.json')

def add_card(source: str, summary: str, priority: str = 'mid'):
    with open(DATA_FILE, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    card = {
        'id': len(data['cards']) + 1,
        'source': source,
        'time': datetime.now().strftime('%H:%M'),
        'summary': summary,
        'priority': priority
    }
    data['cards'].append(card)
    data['updated'] = datetime.now().isoformat()
    
    with open(DATA_FILE, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    
    return card

if __name__ == '__main__':
    if len(sys.argv) < 3:
        print('用法: python add_card.py <来源> <摘要> [low|mid|high]')
        sys.exit(1)
    c = add_card(sys.argv[1], sys.argv[2], sys.argv[3] if len(sys.argv) > 3 else 'mid')
    print(f'✅ [{c["priority"]}] {c["source"]} | {c["summary"]}')
