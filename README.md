## Kubernetes 란?
- 다수의 컨테이너를 효율적으로 배포, 확장, 관리하기 위한 오픈 소스 시스템
- 쿠버네티스는 Docker Compose 와 비슷한 느낌을 가지고 있다. 
- Docker Compose 도 다수의 컨테이너를 
쉽게 관리하기 위해 활용하기 때문이다.
- 쿠버네티스는 Docker Compose 의 확장판 이라고 생각하면 편하다.


## Kubernetes 장점
1. 컨테이너 관리 자동화 (배포, 확장, 업데이트)
2. 부하 분산 (로드 밸런싱)
3. 쉬운 스케일링
4. 셀프 힐링 (어떤 프로그램이 죽으면 재시작 시키는 기능을 가지고 있다)


## Pod 란?
- 도커에서는 하나의 프로그램을 실행시키는 단위를 컨테이너라고 불렀다. 
- 쿠버네티스에서는 하나의 프로그램을 실행시키는 단위를 Pod 라고 부른다.
- 따라서 Pod 는 일반적으로 쿠버네티에서 하나의 프로그램을 실행시키는 단위라고 이해하면 편하다.
- 쿠버네티스에서 가장 작은 단위
- 일반적으로 하나의 파드가 하나의 컨테이너를 가진다
  - 예외적으로 하나의 파드가 여러 개의 컨테이너를 가지는 경우가 있다


## Pod 실행
- 쿠버네티스 환경이 구축되면, 내부에 Pod 라는 것이 띄워지고 이 Pod 안에서 실제로 컨테이너 라는 것을 가지고 있는 구조 
- 쿠버네티스도 Pod 를 띄울때, 도커처럼 로컬이든 도커허브든 이미지를 다운 받아오고 이미지를 기반으로 Pod 를 띄워 실행시킨다.


## 명령어 정리

### Pod 생성
~~~
apply: 뭔가 적용할거다
-f: 파일을

$ kubectl apply -f 매니페스트 파일명
~~~

**Q. 매니페스트 파일 이란?**

쿠버네티스에서 여러가지 리소스를 생성하고 관리하기 위해 작성하는 파일 (예: ~~~.yaml)

### Pod 확인
~~~
$ kubectl get pods
~~~

**Q. Pod 로 띄운 프로그램에 접속이 안되는 이유?**

- 도커는 컨테이너 내부와 컨테이너 외부가 독립적으로 분리되어 있었다
- 쿠버네티스는 Pod 내부의 네트워크를 컨테이너가 공유해서 같이 사용한다.
- Pod 의 네트워크는 로컬 컴퓨터의 네트워크와 독립적으로 분리되어 있다.
  - 이 때문에 Pod 로 띄운 예: Nginx 에 아무리 요청을 보내도 응답이 없던 것

### Pod 접근
1. Pod 내부로 들어가서 접근하기
~~~
$ kubectl exec -it pod명 -- bash

(Pod 내부 접속후)
$ curl localhost:80
~~~

2. Pod 의 내부 네트워크를 외부에서 접속할 수 있도록 포트포워딩(=포트 연결하기)

<img alt="pod_connect" height="300" width="600" src="https://github.com/user-attachments/assets/67f35bb0-341e-4bb4-a91b-f44d29a1e407"/>
<br>

~~~
$ sudo kubectl port-forward pod/pod명 local포트번호:pod포트번호

예: kubectl port-forward pod/nginx-pod 80:80

(참고)
Mac 기준에서 80번 포트에 대해 포트포워딩을 하려면 권한이 필요하기 때문에 sudo 붙여서 실행
sudo 없을시, permission denied 발생
~~~

### Pod 삭제
~~~
$ kubectl delete pod pod명
~~~

### Spring Boot 서버 Pod 로 띄우기

1. 도커 이미지 생성
~~~
프로젝트 build
$ ./gradlew clean build

도커 이미지 생성
$ docker build -t 이미지명 .

도커 이미지 확인
$ docker image ls
~~~

2. Pod 생성
~~~
(매니페스트 파일 작성후)
$ kubectl apply -f 매니페스트 파일명
~~~

Q. Pod 가 잘 생성되었는지 확인시, 아래와 같이 **ImagePullBackOff** 상태의 에러가 발생하는 이유?
~~~
Pod 확인
$ kubectl get pods

(결과)
NAME         READY   STATUS             RESTARTS   AGE
spring-pod   0/1     ImagePullBackOff   0          3s
~~~
