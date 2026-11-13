// 모든 페이지 공통 스크립트 (layout.html 에서 불러온다) — 14168 global.js 를 바탕으로
document.addEventListener("DOMContentLoaded", () => {
    // <a method="DELETE" href="..."> 처럼 method 속성이 있는 링크를 누르면
    // 그 주소로 POST 폼을 만들어 _method(DELETE 등)와 CSRF 토큰을 함께 보낸다.
    // (링크는 원래 GET 만 보낼 수 있으므로)
    document.querySelectorAll("a[method]").forEach((anchor) => {
        // onclick 에 confirm 이 있으면 그것부터 실행하도록 data-onclick 으로 옮긴다
        const onclick = anchor.getAttribute("onclick");
        if (onclick) {
            anchor.removeAttribute("onclick");
            anchor.setAttribute("data-onclick", onclick);
        }

        anchor.addEventListener("click", (e) => {
            e.preventDefault(); // 기본 링크 이동(GET) 막기

            const onclickCode = anchor.getAttribute("data-onclick");
            if (onclickCode && !new Function(onclickCode)()) return; // confirm 에서 취소하면 중단

            const form = document.createElement("form");
            form.action = anchor.getAttribute("href");
            form.method = "POST";

            const addHidden = (name, value) => {
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = name;
                input.value = value;
                form.appendChild(input);
            };
            addHidden("_csrf", document.querySelector('meta[name="_csrf"]')?.getAttribute("content") || "");
            addHidden("_method", anchor.getAttribute("method"));

            document.body.appendChild(form);
            form.submit();
        });
    });
});
