# CNN 
- Input (get image) -> Filter for a feature (convolution) -> Detect (ReLU/activation) -> Condense (enhance features/maxpool)


# Model Research Notes
- AlexNet - 2012, solves problem of regular CNN difficult to train with high res images w/ many classes
- VGG16/19 -> 2014, solves difficulty in converging on deeper networks, so solution is to use smaller networks to converge then it's the base for larger deeper networks (pre-training). Sequential architecture. Cons: Slow to train, large disk/bandwidth needed, lose generalization capability after some depth.
- ResNet - 2015, network-in-network architecture. Deeper than VGG16/19, but smaller model size. Uses GlobAvgPooling. Solves vanishing gradient aka weights are not improving much/no learning done (VGG bottleneck).
- InceptionNet (GoogleNet) - 2015, improves upon weight size compared to VGG/ResNet
- Xception - 2016, Depthwise Separable Convolutions, slightly outperforms InceptionNet on ImageNet dataset w/ more efficient use of model parameters.
- EfficientNet - 2019, good combination of efficiency and accuracy on a variety of scales