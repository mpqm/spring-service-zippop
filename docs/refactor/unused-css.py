import re

def extract_classes_from_html(html_content):
    """
    HTML 파일에서 사용된 클래스명을 추출합니다.
    """
    class_pattern = r'class=["\']([\w\s-]+)["\']'
    classes = re.findall(class_pattern, html_content)
    # 클래스 문자열을 공백 기준으로 분리
    used_classes = set(cls.strip() for group in classes for cls in group.split())
    return used_classes


def extract_classes_from_css(css_content):
    """
    CSS 파일에서 정의된 클래스명을 추출합니다.
    """
    css_pattern = r'\.([a-zA-Z0-9_-]+)\s*\{'
    defined_classes = set(re.findall(css_pattern, css_content))
    return defined_classes


def find_unused_classes(html_file, css_file):
    """
    HTML과 CSS 파일을 비교하여 사용되지 않는 CSS 클래스를 반환합니다.
    """
    with open(html_file, 'r', encoding='utf-8') as html_f:
        html_content = html_f.read()
    with open(css_file, 'r', encoding='utf-8') as css_f:
        css_content = css_f.read()

    used_classes = extract_classes_from_html(html_content)
    defined_classes = extract_classes_from_css(css_content)
    unused_classes = defined_classes - used_classes

    return unused_classes


if __name__ == "__main__":
    # 파일 경로를 설정하세요.
    file_path = "../../frontend/src/components/store/StoreListComponent.vue"  # Vue 템플릿 파일 경로, CSS 파일 경로
    
    unused = find_unused_classes(file_path, file_path)

    print(file_path, "사용되지 않은 CSS 클래스:")
    for cls in unused:
        print(f"- {cls}")

