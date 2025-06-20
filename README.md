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

(Pod 내부에서 나오기)
bash-4.4# exit
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
$ docker build -t 이미지명 도커파일경로 (예: . 은 현재 폴더 경로)

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

이 문제는 **이미지 풀 정책(Image Pull Policy)** 때문에 발생한 것이다.

### 이미지 풀 정책 (Image Pull Policy) 이란?
- 이미지 풀 정책이란? 쿠버네티스가 .yaml 파일을 읽어 Pod 를 생성할때, 이미지를 어떻게 Pull 받아올 것인지에
대한 정책을 의미한다.

1. **Always**
   - 로컬에서 이미지를 가져오지 않고 무조건 레지스트리(예: DockerHub, AWS ECR 같은 원격 이미지 저장소)에서 가져온다.

2. **IfNotPresent**
   - 로컬에서 이미지를 먼저 가져온다. 
   - 만약, 이미지가 없을 경우에만 레지스트리에서 가져온다.

3. **Never**
   - 로컬에서만 이미지를 가져온다.

- 이미지 태그가 **latest** 이거나 **명시되어 있지 않은** 경우 (예: spring-server:latest): imagePullPolicy 는 **Always** 로 설정됨
- 이미지 태그가 **latest** 가 아닌 경우 (예: spring-server:1.0): imagePullPolicy 는 **IfNotPresent** 로 설정됨

따라서 기존 매니페스트 파일은 이미지 태그가 명시되어 있지 않았기 때문에 imagePullPolicy 가 **Always** 로 작동하였을 것이다. 

즉, 로컬에서 이미지를 가져오지 않고 레지스트리에서 가져오려 하여 에러가 발생한 것이다.

예: spring-server 라는 이미지는 DockerHub 같은 저장소에 올린적이 없기 때문에 이미지를 받아오지 못한 것이다.

### 해결: 아래처럼 imagePullPolicy 정책을 설정해주자
~~~
예:
apiVersion: v1
kind: Pod
metadata:
  name: spring-pod
spec:
  containers:
    - name: spring-container
      image: spring-server
      ports:
        - containerPort: 8080
      imagePullPolicy: IfNotPresent
~~~

~~~
* 아래와 같이 Pod 생성 -> 확인 -> 삭제 과정을 진행하자

(Pod 생성)
$ kubectl apply -f spring-pod.yaml

(Pod 확인)
$ kubectl get pods

(Pod 요청 보내기)
방법.1
$ kubectl exec -it spring-pod -- bash

방법.2
$ kubectl port-forward pod/spring-pod 1234:8080

(Pod 삭제)
$ kubectl delete pod spring-pod
~~~

### 프론트엔드(HTML, CSS, Nginx) 서버를 Pod 로 띄우기
~~~
(도커 이미지 생성)
$ docker build -t my-web-server .

(Pod 생성)
$ kubectl apply -f web-server-pod.yaml

(Pod 확인)
$ kubectl get pods

(Pod 요청 보내기)
방법.1
$ kubectl exec -it web-server-pod -- bash

방법.2
$ kubectl port-forward pod/web-server-pod 1234:80

(Pod 삭제)
$ kubectl delete pod web-server-pod
~~~

### 프론트엔드(Next.js) 서버를 Pod 로 띄우기
~~~
(Next.js 서버 프로젝트 생성)
$ npx create-next-app

(도커 이미지 생성)
$ docker build -t next-server .

(Pod 생성)
$ kubectl apply -f next-pod.yaml

(Pod 확인)
$ kubectl get pods

(Pod 요청 보내기)
방법.1
$ kubectl exec -it next-pod -- bash

방법.2
$ kubectl port-forward pod/next-pod 1234:3000

(Pod 삭제)
$ kubectl delete pod next-pod
~~~
