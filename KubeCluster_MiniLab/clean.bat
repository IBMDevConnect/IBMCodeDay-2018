kubectl delete --ignore-not-found=true svc,pvc,deployment -l app=wordpress

kubectl delete --ignore-not-found=true secret mysql-pass

kubectl delete --ignore-not-found=true -f wordpress-deployment.yaml

kubectl delete --ignore-not-found=true -f mysql-deployment.yaml

kubectl delete --ignore-not-found=true -f local-volumes.yaml
