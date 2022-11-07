# Imports
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import LabelEncoder
from keras.models import Sequential
from keras import layers
from keras.backend import clear_session

# Configure Filepaths
filepath_dict = {'anxiety': 'reddit-data/anxiety.csv',
                 'depression': 'reddit-data/depression.csv',
                 'tourettes': 'reddit-data/tourettes.csv'}

df_list = []

# Create the master-set
for source, filepath in filepath_dict.items():
    df = pd.read_csv(filepath, names=['selftext'])
    df = df[df.selftext.notnull()]  # Remove empty values
    df = df[df.selftext != '']  # Remove empty strings
    df = df[df.selftext != '[deleted]']  # Remove deleted status posts
    df = df[df.selftext != '[removed]']  # Remove removed status posts
    df['category'] = source  # Add category column
    df_list.append(df)

df = pd.concat(df_list)
# print(f'Summary: {df.info}\nDescription: {df.describe()}\nShape: {df.shape}')

# Make master-set csv
df.to_csv('reddit-data/master-set.csv', index=0)
df.info()  # Get info on dataset

# Create feature matrix using bag-of-words model
sentences = df['selftext'].values
y = df['category'].values

le = LabelEncoder()  # Convert strings to number format
y = le.fit_transform(y)

sentences_train, sentences_test, y_train, y_test = train_test_split(sentences, y, test_size=0.25, random_state=1000)

# Create the training and test set using the vectorized data
vectorizer = CountVectorizer()
vectorizer.fit(sentences_train)

X_train = vectorizer.transform(sentences_train)
X_test = vectorizer.transform(sentences_test)

# Setup logistic regression classification model and train
classifier = LogisticRegression()
classifier.fit(X_train, y_train)

score = classifier.score(X_test, y_test)
print("Accuracy: ", score)

# Keras Sequential Model
input_dim = X_train.shape[1]  # Number of features

# Create sequential model with TensorFlow background
model = Sequential()
model.add(layers.Dense(10, input_dim=input_dim, activation='relu'))
model.add(layers.Dense(1, activation='sigmoid'))
model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])
model.summary()

# Reset training
clear_session()

# Gets the computed weights from the previous training
history = model.fit(X_train, y_train,
                    epochs=100,
                    verbose=False,
                    validation_data=(X_test, y_test),
                    batch_size=10)


# Evaluate accuracy of keras sequential model
loss, accuracy = model.evaluate(X_train, y_train, verbose=False)
print("Training Accuracy: {:.4f}".format(accuracy))
loss, accuracy = model.evaluate(X_test, y_test, verbose=False)
print("Testing Accuracy:  {:.4f}".format(accuracy))

# Visualize the loss and accuracy for training and testing data
plt.style.use('ggplot')


def plot_history(history):
    print(history.history)


    acc = history.history['accuracy']
    val_acc = history.history['val_accuracy']
    loss = history.history['loss']
    val_loss = history.history['val_loss']
    x = range(1, len(acc) + 1)

    plt.figure(figsize=(12, 5))
    plt.subplot(1, 2, 1)
    plt.plot(x, acc, 'b', label='Training acc')
    plt.plot(x, val_acc, 'r', label='Validation acc')
    plt.title('Training and validation accuracy')
    plt.legend()
    plt.subplot(1, 2, 2)
    plt.plot(x, loss, 'b', label='Training loss')
    plt.plot(x, val_loss, 'r', label='Validation loss')
    plt.title('Training and validation loss')
    plt.legend()
    plt.show()


plot_history(history)
